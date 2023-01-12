SUMMARY = "Chromium OS Region Data"
DESCRIPTION = "Chromium OS Region Data"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/regions/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

require recipes-chromeos/chromiumos-platform2/chromiumos-platform2.inc

S = "${WORKDIR}/src/platform2/${BPN}"
B = "${WORKDIR}/build"
PR = "r2027"

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
PACKAGECONFIG[crosdebug] = ""

GN_ARGS += ' \
    use={ \
        crosdebug=${@bb.utils.contains('PACKAGECONFIG', 'crosdebug', 'true', 'false', d)} \
    } \
'

do_compile() {
    cd "${S}"
    ./regions.py --format=json --output "${B}/cros-regions.json" ${@bb.utils.contains('PACKAGECONFIG', 'crosdebug', '--include_pseudolocales', '', d)}
}

FILES:${PN} += "${datadir}/misc"
do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/cros_region_data ${D}${bindir}/

    install -d ${D}${datadir}/misc
    install -m 0644 cros-regions.json "${D}${datadir}"/misc/
}

