SUMMARY = "U2FHID Emulation Daemon"
DESCRIPTION = "U2FHID Emulation Daemon"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/u2fd/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn platform

# dbusxx-xml2cpp is provided by libdbus-c++-native
# protoc is provided by protbuf-native
# protoc-gen-go is provided by github.com-golang-protobuf-native
# go-generate-chromeos-dbus-bindings is provided by chromeos-dbus-bindings-native
DEPENDS:append = "\
    attestation-client \
    cbor \
    cryptohome-client \
    github.com-golang-protobuf-native \
    libdbus-c++-native \
    libbrillo \
    libchrome \
    libhwsec \
    metrics \
    power-manager-client \
    protobuf-native \
    session-manager-client \
    u2fd-client \
"

S = "${WORKDIR}/src/platform2/${BPN}"
B = "${WORKDIR}/build"
PR = "r1532"

CXXFLAGS:append = " -Wno-error=implicit-int-float-conversion"

require recipes-chromeos/files/include/common-mk-update-mm.inc

GN_ARGS += 'platform_subdir="${BPN}"'

PACKAGECONFIG ??= ""

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
PACKAGECONFIG[fuzzer] = ""
PACKAGECONFIG[profiling] = ""
PACKAGECONFIG[tcmalloc] = ""
PACKAGECONFIG[tpm] = ""
PACKAGECONFIG[cr50_onboard] = ""
PACKAGECONFIG[ti50_onboard] = ""
PACKAGECONFIG[test] = ""

GN_ARGS += ' \
    use={ \
        cros_host=${@bb.utils.contains('PACKAGECONFIG', 'cros_host', 'true', 'false', d)} \
        fuzzer=${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)} \
        profiling=${@bb.utils.contains('PACKAGECONFIG', 'profiling', 'true', 'false', d)} \
        tcmalloc=${@bb.utils.contains('PACKAGECONFIG', 'tcmalloc', 'true', 'false', d)} \
        tpm=${@bb.utils.contains('PACKAGECONFIG', 'tpm', 'true', 'false', d)} \
        cr50_onboard=${@bb.utils.contains('PACKAGECONFIG', 'cr50_onboard', 'true', 'false', d)} \
        ti50_onboard=${@bb.utils.contains('PACKAGECONFIG', 'ti50_onboard', 'true', 'false', d)} \
        test=${@bb.utils.contains('PACKAGECONFIG', 'test', 'true', 'false', d)} \
    } \
'

do_compile() {
    ninja -C ${B}
}

python do_install:append() {
    bb.build.exec_func("do_install_u2fd", d)
}

do_install_u2fd() {
    install -d "${D}${bindir}"
    install -m 0755 u2fd "${D}${bindir}/"

    install -d "${D}${sysconfdir}/init/"
    install -m 0644 "${S}"/init/*.conf "${D}${sysconfdir}/init/"
}
