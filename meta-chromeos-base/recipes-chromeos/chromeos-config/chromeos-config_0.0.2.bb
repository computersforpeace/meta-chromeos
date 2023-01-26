SUMMARY = "Chromium OS-specific configuration"
DESCRIPTION = "Chromium OS-specific configuration"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/config/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

# TODO / HACK: these are hard-coded config files for an amd64 VM.
SRC_URI += "file://configfs.img"
SRC_URI += "file://config.yaml"
SRC_URI += "file://identity.bin"

PR = "r192"

RDEPENDS:${PN} += "crosid"

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
PACKAGECONFIG[zephyr_poc] = ""

do_compile() {
    :
}

do_install() {
    install -d "${D}${datadir}/chromeos-config/yaml"

    install -m 0644 "${WORKDIR}"/configfs.img "${D}${datadir}/chromeos-config/"
    install -m 0644 "${WORKDIR}"/identity.bin "${D}${datadir}/chromeos-config/"
    install -m 0644 "${WORKDIR}"/config.yaml "${D}${datadir}/chromeos-config/yaml/"
}

