SUMMARY = "Chrome OS root block device tool/library"
DESCRIPTION = "Chrome OS root block device tool/library"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/third_party/rootdev/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

SRC_URI += "file://write_gpt.sh"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 "${WORKDIR}"/write_gpt.sh ${D}${sbindir}/
}
