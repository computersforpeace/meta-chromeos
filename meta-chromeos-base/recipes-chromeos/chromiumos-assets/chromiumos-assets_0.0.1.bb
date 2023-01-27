SUMMARY = "Chromium OS-specific assets"
DESCRIPTION = "Chromium OS-specific assets"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform/chromiumos-assets"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

require recipes-chromeos/chromiumos-platform/chromiumos-platform-${BPN}.inc

S = "${WORKDIR}/src/platform/${BPN}"
B = "${WORKDIR}/build"
PR = "r16"

do_install() {
    install -d "${D}${datadir}/chromeos-assets/images"
    install -m 0644 "${S}"/images/* "${D}${datadir}/chromeos-assets/images/"

    install -d "${D}${datadir}/chromeos-assets/images_100_percent"
    install -m 0644 "${S}"/images_100_percent/* "${D}${datadir}/chromeos-assets/images_100_percent/"

    install -d "${D}${datadir}/chromeos-assets/images_200_percent"
    install -m 0644 "${S}"/images_200_percent/* "${D}${datadir}/chromeos-assets/images_200_percent/"
}
FILES:${PN} += "${datadir}/chromeos-assets"
