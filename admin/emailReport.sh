#!/bin/bash

REPORT_FILE="/tmp/report_file.txt"

/home/muldoon/repos/sage/admin/generateDailyGameTableUsageReport.sh > $REPORT_FILE

/bin/mail -s '24 hour game table usage report' 'logs@infrno.net'  < $REPORT_FILE

