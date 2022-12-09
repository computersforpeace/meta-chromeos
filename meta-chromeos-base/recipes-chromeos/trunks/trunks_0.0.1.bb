SUMMARY = "Trunks service for Chromium OS"
DESCRIPTION = "Trunks service for Chromium OS"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/trunks/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn

S = "${WORKDIR}/src/platform2/${BPN}"
B = "${WORKDIR}/build"
PR = "r3280"

FILES:${PN} += "/usr/share/policy"

DEPENDS += "\
    chromeos-ec-headers \
    compiler-rt-sanitizers \
    libbrillo \
    libchrome \
    libhwsec-foundation \
    libmetrics \
    minijail \
    openssl \
    power-manager-client \
    protobuf \
    protobuf-native \
    system-api \
    tpm2 \
"

RDEPENDS:${PN} += "\
    libhwsec-foundation \
    libminijail \
"

GN_ARGS += 'platform_subdir="${BPN}"'

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('MACHINE_FEATURES', 'tpm tpm2', d)} \
    cr50_onboard pinweaver_csme tpm2_simulator"

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
PACKAGECONFIG[cr50_onboard] = ",,,chromeos-cr50,,"
PACKAGECONFIG[cros_host] = ""
PACKAGECONFIG[csme_emulator] = ""
PACKAGECONFIG[ftdi_tpm] = ",,libftdi,libftdi,,"
PACKAGECONFIG[fuzzer] = ",,gtest,,,"
PACKAGECONFIG[generic_tpm2] = ",,,chromeos-cr50-scripts,,"
PACKAGECONFIG[pinweaver_csme] = ""
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[test] = ",,tpm2,,,"
PACKAGECONFIG[ti50_onboard] = ",,,chromeos-ti50,,"
PACKAGECONFIG[tpm_dynamic] = ""
PACKAGECONFIG[tpm2_simulator] = ",,tpm2-simulator,,,"

GN_ARGS += ' \
    use={ \
        cr50_onboard=${@bb.utils.contains('PACKAGECONFIG', 'cr50_onboard', 'true', 'false', d)} \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        csme_emulator=${@bb.utils.contains('PACKAGECONFIG', 'csme_emulator', 'true', 'false', d)} \
        ftdi_tpm=${@bb.utils.contains('PACKAGECONFIG', 'ftdi_tpm', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        generic_tpm2=${@bb.utils.contains('PACKAGECONFIG', 'generic_tpm2', 'true', 'false', d)} \
        pinweaver_csme=${@bb.utils.contains('PACKAGECONFIG', 'pinweaver_csme', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
        ti50_onboard=${@bb.utils.contains('PACKAGECONFIG', 'ti50_onboard', 'true', 'false', d)} \
        tpm_dynamic=${@bb.utils.contains('PACKAGECONFIG', 'tpm_dynamic', 'true', 'false', d)} \
        tpm2_simulator=${@bb.utils.contains('PACKAGECONFIG', 'tpm2_simulator', 'true', 'false', d)} \
    } \
'

CXX:append = " -I${STAGING_INCDIR} -I${STAGING_INCDIR}/power_manager-client -I${STAGING_LIBDIR}/clang/*/include/"

do_compile() {
    ninja -C ${B}
}

do_install() {
    install -D -m 0644 -t ${D}/etc/dbus-1/system.d/ ${S}/org.chromium.Trunks.conf

    if ${@bb.utils.contains('PACKAGECONFIG', 'cr50_onboard', 'true', 'false', d)} ||
       [ ${@bb.utils.contains('PACKAGECONFIG', 'ti50_onboard', 'true', 'false', d)} ]; then
        install -D -m 0644 ${S}/trunksd.conf.cr50 ${D}/etc/init/trunksd.conf
    else
        install -D -m 0644 ${S}/trunksd.conf ${D}/etc/init/trunksd.conf
    fi

    if ${@bb.utils.contains('PACKAGECONFIG', 'tpm_dynamic', 'true', 'false', d)}; then
        sed -i '/env TPM_DYNAMIC=/s:=.*:=true:' \
            "${D}/etc/init/trunksd.conf"
    fi

    install -D -t ${D}${sbindir} pinweaver_client
    install -D -t ${D}${sbindir} trunks_client
    install -D -t ${D}${sbindir} trunks_send
    if ${@bb.utils.contains('PACKAGECONFIG', 'tpm_dynamic', 'true', 'false', d)}; then
        install -D ${S}/tpm_version ${D}${sbindir}/tpm2_version
    else
        install -D -t ${D}${sbindir} ${S}/tpm_version
    fi
    install -D -t ${D}${sbindir} trunksd
    install -D lib/libtrunks.so ${D}${libdir}/libtrunks.so.${SO_VERSION}
    ln -sf libtrunks.so.${SO_VERSION} ${D}${libdir}/libtrunks.so

    # trunks_test library implements trunks mocks which are used by unittest
    # and fuzzer.
    # HACK: 'test' and 'fuzzer' aren't ready.
    if false && ( [ ${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} ] ||
                  [ ${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} ] ); then
        install -D lib/libtrunks_test.a ${D}${libdir}/libtrunks_test.a
        install -D lib/libtrunks_lib.a ${D}${libdir}/libtrunks_lib.a
    fi

    ARCH=${TARGET_ARCH}
    [ "${ARCH}" = x86_64 ] && ARCH=amd64

    if ${@bb.utils.contains('PACKAGECONFIG', 'pinweaver_csme', 'true', 'false', d)} &&
       [ ${@bb.utils.contains('PACKAGECONFIG', 'generic_tpm2', 'true', 'false', d)} ]; then
        install -D -m 0644 ${S}/csme/tpm_tunneld.conf ${D}/etc/init/tpm_tunneld.conf

        install -D -t ${D}${sbindir} pinweaver_provision
        install -D -t ${D}${sbindir} tpm_tunneld
        install -D -m 0644 ${S}/csme/tpm_tunneld-seccomp-${ARCH}.policy ${D}/usr/share/policy/tpm_tunneld-seccomp.policy
    fi

    install -D -m 0644 ${S}/trunksd-seccomp-${ARCH}.policy ${D}/usr/share/policy/trunksd-seccomp.policy

    install -D -m 0644 -t ${D}${includedir}/trunks/ ${S}/*.h
    install -D -m 0644 -t ${D}${includedir}/trunks/ ./gen/include/trunks/*.h

    install -D -m 0644 -t ${D}${includedir}/trunks/csme/ ${S}/csme/pinweaver_provision.h
    install -D -m 0644 -t ${D}${includedir}/trunks/csme/ ${S}/csme/pinweaver_provision_impl.h

    install -D -m 0644 -t ${D}${includedir}/proto/ ${S}/pinweaver.proto

    install -D -m 0644 -t ${D}${includedir}/chromeos/dbus/trunks/ ${S}/trunks_interface.proto

    install -d ${D}${libdir}/pkgconfig
    install -m 0644 obj/trunks/libtrunks.pc ${D}${libdir}/pkgconfig/
}

