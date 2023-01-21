SUMMARY = "ChromeOS vital product data utilities"
DESCRIPTION = "ChromeOS vital product data utilities"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform/vpd/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit pkgconfig

SRC_URI += " \
    git://chromium.googlesource.com/chromiumos/platform/vpd;protocol=https;branch=main \
"
SRCREV = "256955cdec97e1fc3b99e8af1723e033690526f4"

DEPENDS:append = "\
    util-linux \
"

S = "${WORKDIR}/git"
PR = "r155"

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
PACKAGECONFIG[static] = ""
PACKAGECONFIG[systemd] = ""

do_compile() {
    oe_runmake vpd # TODO: also vpd_s and vpd_test? i.e., 'all'
}

do_install() {
    install -d "${D}${sbindir}"
    install -m 0755 vpd "${D}${sbindir}/"
    # TODO: more; init files? static?
}

