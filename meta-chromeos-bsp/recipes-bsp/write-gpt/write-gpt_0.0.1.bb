SUMMARY = "Chrome OS root block device tool/library"
DESCRIPTION = "Chrome OS root block device tool/library"
HOMEPAGE = "https://chromium.googlesource.com/chromiumos/third_party/rootdev/"
LICENSE = "BSD-3-Clause"
#LIC_FILES_CHKSUM = "file://LICENSE;md5=562c740877935f40b262db8af30bca36"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${THISDIR}/files/write_gpt.sh ${D}${sbindir}/
}
