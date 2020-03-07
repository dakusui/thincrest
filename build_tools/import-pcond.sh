#!/usr/bin/env bash
set -E -o nounset -o errexit +o posix -o pipefail
shopt -s inherit_errexit

# shellcheck disable=SC1090
source "$(dirname "${0}")/lib/mvn-utils.rc"

function mangle_package() {
  local _basedir="${1}"
  mv "${_basedir}/com/github/dakusui/pcond" "${_basedir}/com/github/dakusui/thincrest_pcond"
  find "${_basedir}" -type f -name '*.java' \
    -exec sed -i 's/com\.github\.dakusui\.pcond/com.github.dakusui.thincrest_pcond/g' {} \;
}

function main() {
  local _out=./target/generated-sources/local
  mkdir -p "${_out}"
  mvn-unpack com.github.dakusui:pcond:1.0-SNAPSHOT:jar:sources "${_out}"
  mangle_package "${_out}"
}

main
