SUMMARY = "This command-line tool lets clients upload images to gold"
DESCRIPTION = "This command-line tool lets clients upload images to gold"
HOMEPAGE = "https://skia.googlesource.com/buildbot/+/HEAD/gold-client/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit chromeos_gn
PR = "r1"


do_compile() {
    ninja -C ${B} ${BPN}
}

do_install() {
    :
}
