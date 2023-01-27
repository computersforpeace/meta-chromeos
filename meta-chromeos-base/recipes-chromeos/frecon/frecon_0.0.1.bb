SUMMARY = "ChromeOS KMS console"
DESCRIPTION = "ChromeOS KMS console"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/platform/frecon"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${CHROMEOS_COMMON_LICENSE_DIR}/BSD-Google;md5=29eff1da2c106782397de85224e6e6bc"

inherit pkgconfig

SRC_URI += " \
    git://chromium.googlesource.com/chromiumos/platform/frecon;protocol=https;branch=main \
"
SRCREV = "6ec04b2b8d1e12dc685c3375df225c17bba94c3f"

DEPENDS:append = "\
    dbus \
    libpng \
    libtsm \
    libdrm \
    eudev \
"

S = "${WORKDIR}/git"
PR = "r187"

do_compile() {
    export PKG_CONFIG=pkg-config
    oe_runmake
}

do_install() {
    install -d "${D}${sysconfdir}/dbus-1/system.d"
    install -m 0644 "${S}/dbus/org.chromium.frecon.conf" "${D}${sysconfdir}/dbus-1/system.d/"

    oe_runmake install DESTDIR="${D}"
}

