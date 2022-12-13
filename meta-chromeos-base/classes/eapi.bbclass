dosbin() {
  (
    #cd "${B}"
    _OUTDIR=${D}${INTO}${sbindir}
    install -d ${_OUTDIR}
    for i in "$@"; do
      install -m 0755 "$i" ${_OUTDIR}
    done
  )
}

dobin() {
  (
    #cd "${B}"
    _OUTDIR=${D}${INTO}${bindir}
    install -d ${_OUTDIR}
    for i in "$@"; do
      install -m 0755 "$i" ${_OUTDIR}
    done
  )
}

dolib_so() {
  (
    #cd "${B}"
    fn=$(basename "$1")
    SO_VERSION=${SO_VERSION:-1}
    _OUTDIR=${D}${INTO}${libdir}
    install -d ${_OUTDIR}
    install -m 0755 "$1" ${_OUTDIR}/${fn}.${SO_VERSION}
    ln -sf ${fn}.${SO_VERSION} ${_OUTDIR}/${fn}
  )
}

doins() {
    _OUTDIR=${D}${INSINTO}
    install -d ${_OUTDIR}
    install "$1" ${_OUTDIR}/
}

#INSINTO ??= "/"
#insinto() {
#INSINTO="$1"
#}

export INTO
INTO="/"
into() {
  INTO="$1"
}

export EXEINTO
EXEINTO="/"
exeinto() {
  EXEINTO="$1"
}
