FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-keyutils-1.6.1-fix-private-keyword-usage.patch"
SRC_URI += "file://0002-cplusplusextern.patch"

