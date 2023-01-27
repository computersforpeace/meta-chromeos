SUMMARY = "Terminal Emulator State Machine"
DESCRIPTION = "Terminal Emulator State Machine"
HOMEPAGE = "http://cgit.freedesktop.org/~dvdhrm/libtsm"
LICENSE = "LGPL-2.1-or-later & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=be41eca402c741d9a6384aea14c75ae3"

SRC_URI += "http://www.freedesktop.org/software/kmscon/releases/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "114115d84a2bc1802683871ea2d70a16ddeec8d2f8cde89ebd2046d775e6cf07"
SRC_URI += "\
    file://0001-libtsm-add-OSC-string-callback.patch \
    file://0002-libtsm-do-not-reset-scrollback-position-and-age-if-i.patch \
"

inherit autotools

EXTRA_OECONF += "--enable-debug=no"

do_configure() {
    oe_runconf
}
