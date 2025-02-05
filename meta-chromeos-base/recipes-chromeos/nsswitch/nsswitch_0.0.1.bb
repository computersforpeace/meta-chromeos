SUMMARY = "Provide the Name Service Switch configuration file for glibc"
DESCRIPTION = "Provide the Name Service Switch configuration file for glibc"
HOMEPAGE = "http://www.chromium.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

PR = "r1"

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
PACKAGECONFIG[zeroconf] = ""

GN_ARGS += ' \
    use={ \
        zeroconf=${@bb.utils.contains('PACKAGECONFIG', 'zeroconf', 'true', 'false', d)} \
    } \
'

do_install() {
    install -d ${D}${sysconfdir}
    if ${@bb.utils.contains('PACKAGECONFIG', 'fuzzer', 'true', 'false', d)}; then
        f=${THISDIR}/files/nsswitch.mdns.conf
    else
        f=${THISDIR}/files/nsswitch.default.conf
    fi
    install -m 0644 "${f}" ${D}${sysconfdir}/nsswitch.conf
}
