#!/bin/sh

grep -h '^import ideal.development' * | sort -u | cut -d. -f3-
