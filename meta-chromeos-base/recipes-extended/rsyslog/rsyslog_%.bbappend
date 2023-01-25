FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://001-add-imstdoutsock-plugin.patch"
SRC_URI += "file://001-add-imstdoutsock-plugin/"

do_load_sources() {
  cp -rf "${WORKDIR}/001-add-imstdoutsock-plugin" "${S}/plugins/imstdoutsock"
}

addtask do_load_sources after do_unpack before do_configure
