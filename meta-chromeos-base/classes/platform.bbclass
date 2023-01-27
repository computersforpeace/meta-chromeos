DEPENDS += "gn-native"

FILES:${PN} += "/usr/share/policy"
def platform_install(d):
    import collections
    import itertools
    import json
    import os

    # TODO: make CHROMEOS_PN mandatory?
    target = d.getVar('CHROMEOS_PN') or d.getVar('PN')

    arch = d.expand('${TARGET_ARCH}')
    if arch == 'x86_64':
        arch = 'amd64'

    env = os.environ
    env['ARCH'] = arch

    out, _ = bb.process.run(['gn', 'desc', d.expand("${B}"), '//%s/*' % target, '--root=' + d.expand("${WORKDIR}/src/platform2"), '--args=' + d.expand("${GN_ARGS}"), '--format=json', '--all'], env=env)

    j = json.loads(out)
    bb.debug(1, 'out = %s' % out)
    bb.debug(1, 'json = %s' % j)

    deps = j.get("//%s:all" % target, {}).get("deps", [])
    config_group = collections.defaultdict(list)
    for dep in deps:
        install_config = j.get(dep, {}).get("metadata", {}).get("_install_config")
        if not install_config:
            continue
        install_config = install_config[0]
        sources = install_config.get("sources")
        if not sources:
            continue
        install_path = install_config.get("install_path")
        outputs = install_config.get("outputs")
        symlinks = install_config.get("symlinks")
        recursive = install_config.get("recursive")
        options = install_config.get("options")
        command_type = install_config.get("type")
        do_glob = install_config.get("glob")
        tree_relative_to = install_config.get("tree_relative_to")

        if do_glob:
            sources = list(
              itertools.chain.from_iterable(
                # glob is always recursive to support **.
                glob.glob(x, recursive=True)
                for x in sources
                )
              )
        if tree_relative_to:
            for source in sources:
                new_install_path = install_path
                relpath = os.path.relpath(source, tree_relative_to)
                new_install_path = os.path.join(
                    install_path, os.path.dirname(relpath)
                )
                config_key = (
                    new_install_path,
                    recursive,
                    options,
                    command_type,
                )
                config_group[config_key].append(
                    ([source], outputs, symlinks)
                )
        else:
            config_key = (install_path, recursive, options, command_type)
            config_group[config_key].append((sources, outputs, symlinks))

    cmd_list = []
    for install_config, install_args in config_group.items():
        args = []
        # Commands to install sources without explicit outputs nor symlinks
        # can be merged into one. Concat all such sources.
        sources = sum(
            [
                sources
                for sources, outputs, symlinks in install_args
                if not outputs and not symlinks
            ],
            [],
        )
        if sources:
            args.append((sources, None, None))
        # Append all remaining sources/outputs/symlinks.
        args += [
            (sources, outputs, symlinks)
            for sources, outputs, symlinks in install_args
            if outputs or symlinks
        ]
        # Generate the command line.
        install_path, recursive, options, command_type = install_config
        for sources, outputs, symlinks in args:
            if not command_type:
                if not symlinks:
                    install_type = "ins"
                else:
                    install_type = "sym"
                    if install_path:
                        outputs = [
                            os.path.join(install_path, symlink) for symlink in symlinks
                        ]
                    else:
                        outputs = symlinks
                cmd_list += [['install', '-D', '-m', '0644', '-t', d.expand("${D}") + install_path] + sources]
            elif command_type == "executable":
                pass
            elif command_type == "static_library":
                if install_path != "lib":
                    bb.fatal('Unimplemented shared library path: %s' % install_path)
                install_path = d.expand("${libdir}")
                cmd_list += [['install', '-D', '-m', '0755', '-t', d.expand('${D}') + install_path] + sources]
            elif command_type == "shared_library":
                #for s, o in zip(sources, outputs):
                    #if o == "lib":
                        #o = d.expand("${libdir}")
                    #cmd_list += ['install', '-D', '-m', '0755', s, d.expand("${D}") + o]
                if install_path == 'lib':
                    install_path = d.expand("${libdir}")
                for s in sources:
                    so_name = os.path.basename(s)
                    so_name_ver = so_name + "." + (d.getVar("SO_VERSION") or "1")
                    cmd_list += [
                        ['install', '-D', '-m', '0755', s, os.path.join(d.expand("${D}" + install_path), so_name_ver)],
                        ['ln', '-sf', so_name_ver, os.path.join(d.expand("${D}" + install_path), so_name)],
                    ]
                #cmd_list += [['install', '-D', '-m', '0755', '-t', d.expand("${D}") + install_path] + sources]
            else:
                bb.fatal('Unimplemented command type: %s' % command_type)

    bb.debug(1, 'cmd_list = %s' % cmd_list)
    for cmd in cmd_list:
        bb.process.run(cmd)


python platform_do_install() {
    platform_install(d)
}

platform_install_dbus_client_lib() {
    libname=${1:-${PN}}

    client_includes=${includedir}/${libname}-client
    client_test_includes=${includedir}/${libname}-client-test

    # Install DBus proxy headers.
    install -d ${D}${client_includes}/${libname}
    install -m 0644 ${B}/gen/include/${libname}/dbus-proxies.h \
                    ${D}${client_includes}/${libname}/
    install -d ${D}${client_test_includes}/${libname}
    install -m 0644 ${B}/gen/include/${libname}/dbus-proxy-mocks.h \
                    ${D}${client_test_includes}/${libname}/

    if [ -f "${S}/lib${libname}-client.pc.in" ]; then
        # Install pkg-config for client libraries
        install -d ${D}${libdir}/pkgconfig
        sed \
	   -e "s|@PV@|${PV}|g" \
           -e "s|@INCLUDE_DIR@|${client_includes}|g" \
              "${S}/lib${libname}-client.pc.in" > "${D}${libdir}/pkgconfig/lib${libname}-client.pc"

        sed \
	   -e "s|@PV@|${PV}|g" \
           -e "s|@INCLUDE_DIR@|${client_test_includes}|g" \
              "${S}/lib${libname}-client-test.pc.in" > "${D}${libdir}/pkgconfig/lib${libname}-client-test.pc"
    fi
}

EXPORT_FUNCTIONS install_dbus_client_lib platform_install do_install
