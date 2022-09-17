SUMMARY = "Crypto and utility functions used in TPM related daemons."
DESCRIPTION = "Crypto and utility functions used in TPM related daemons."
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/libhwsec/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn python3native

S = "${WORKDIR}/src/platform2/${BPN}"
B = "${WORKDIR}/build"
PR = "r430"

export SO_VERSION="1"

DEPENDS:append = " abseil-cpp flatbuffers libbrillo libchrome libtpm-manager-client openssl protobuf system-api trunks"

# Need native flatc.
do_compile[depends] += "flatbuffers-native:do_populate_sysroot"
do_compile[depends] += "python3-jinja2-native:do_populate_sysroot"

GN_ARGS += 'platform_subdir="${BPN}"'

PACKAGECONFIG ??= "${@bb.utils.filter('MACHINE_FEATURES', 'tpm tpm2', d)}"

# Description of all the possible PACKAGECONFIG fields (comma delimited):
# 1. Extra arguments that should be added to the configure script argument list (EXTRA_OECONF or PACKAGECONFIG_CONFARGS) if the feature is enabled.
# 2. Extra arguments that should be added to EXTRA_OECONF or PACKAGECONFIG_CONFARGS if the feature is disabled.
# 3. Additional build dependencies (DEPENDS) that should be added if the feature is enabled.
# 4. Additional runtime dependencies (RDEPENDS) that should be added if the feature is enabled.
# 5. Additional runtime recommendations (RRECOMMENDS) that should be added if the feature is enabled.
# 6. Any conflicting (that is, mutually exclusive) PACKAGECONFIG settings for this feature.

# Empty PACKAGECONFIG options listed here to avoid warnings.
# The .bb file should use these to conditionally add patches,
# command-line switches and dependencies.
PACKAGECONFIG[cros_host] = ""
PACKAGECONFIG[fuzzer] = ",,trousers trunks,,,"
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ",,trunks tpm2-simulator,,,"
PACKAGECONFIG[tpm] = ",,trousers,trousers,,"
PACKAGECONFIG[tpm2] = ",,trunks,trunks,,"
PACKAGECONFIG[tpm_dynamic] = ""

GN_ARGS += ' \
    use={ \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
        tpm=${@bb.utils.contains('PACKAGECONFIG', 'tpm', 'true', 'false', d)} \
        tpm2=${@bb.utils.contains('PACKAGECONFIG', 'tpm2', 'true', 'false', d)} \
        tpm_dynamic=${@bb.utils.contains('PACKAGECONFIG', 'tpm_dynamic', 'true', 'false', d)} \
    } \
'

do_compile() {
    ninja -C ${B} libhwsec:all
}

python do_install() {
    import collections
    import itertools
    import json
    import os

    target = 'libhwsec'

    out, _ = bb.process.run(['gn', 'desc', d.expand("${B}"), '//libhwsec/*', '--root=' + d.expand("${WORKDIR}/src/platform2"), '--args=' + d.expand("${GN_ARGS}"), '--format=json', '--all'])

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
            elif command_type == "shared_library":
                #for s, o in zip(sources, outputs):
                    #if o == "lib":
                        #o = d.expand("${libdir}")
                    #cmd_list += ['install', '-D', '-m', '0755', s, d.expand("${D}") + o]
                if install_path == 'lib':
                    install_path = d.expand("${libdir}")
                for s in sources:
                    so_name = os.path.basename(s)
                    so_name_ver = so_name + d.expand(".${SO_VERSION}")
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
}
