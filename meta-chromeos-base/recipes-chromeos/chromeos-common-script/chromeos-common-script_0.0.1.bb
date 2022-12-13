SUMMARY = "Chrome OS storage info tools"
DESCRIPTION = "Chrome OS storage info tools"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform2/+/HEAD/chromeos-common-script/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn

CHROMEOS_PN = "${BPN}"

S = "${WORKDIR}/src/platform2/${BPN}"
B = "${WORKDIR}/build"
PR = "r395"

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
PACKAGECONFIG[direncryption] = ""
PACKAGECONFIG[fsverity] = ""
PACKAGECONFIG[kernel4_4] = ""
PACKAGECONFIG[prjquota] = ""

GN_ARGS += ' \
    use={ \
        direncryption=${@bb.utils.contains('PACKAGECONFIG', 'direncryption', 'true', 'false', d)} \
        fsverity=${@bb.utils.contains('PACKAGECONFIG', 'fsverity', 'true', 'false', d)} \
        kernel4_4=${@bb.utils.contains('PACKAGECONFIG', 'kernel4_4', 'true', 'false', d)} \
        prjquota=${@bb.utils.contains('PACKAGECONFIG', 'prjquota', 'true', 'false', d)} \
    } \
'

FILES:${PN} += "${datadir}"
do_install() {
    install -d ${D}${datadir}/misc
    install -m 0644 ${S}/share/chromeos-common.sh ${D}${datadir}/misc
    install -m 0644 ${S}/share/lvm-utils.sh ${D}${datadir}/misc
    if ${@bb.utils.contains('PACKAGECONFIG', 'direncryption', 'true', 'false', d)}; then
        sed -i '/local direncryption_enabled=/s/false/true/' \
            "${D}/usr/share/misc/chromeos-common.sh" ||
            bbfatal "Can not set directory encryption in common library"
    fi
    if ${@bb.utils.contains('PACKAGECONFIG', 'fsverity', 'true', 'false', d)}; then
        sed -i '/local fsverity_enabled=/s/false/true/' \
            "${D}/usr/share/misc/chromeos-common.sh" ||
            bbfatal "Can not set fs-verity in common library"
    fi
    if ${@bb.utils.contains('PACKAGECONFIG', 'prjquota', 'true', 'false', d)}; then
        sed -i '/local prjquota_enabled=/s/false/true/' \
            "${D}/usr/share/misc/chromeos-common.sh" ||
            bbfatal "Can not set project quota in common library"
    fi
}

