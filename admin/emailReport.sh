#!/bin/bash

REPORT_FILE="/tmp/report_file.txt"

/home/muldoon/repos/sage/admin/generateDailyGameTableUsageReport.sh > $REPORT_FILE

#/bin/mail -s '24 hour game table usage report' 'server_logs@infrno.net, matthew@infrno.net'  < $REPORT_FILE
/bin/mail -s '24 hour game table usage report' 'server_logs@infrno.net'  < $REPORT_FILE

