#!/bin/bash

#export JAVA_HOME=/home/enav/jre1.8.0_20
#export PATH=$JAVA_HOME/bin:$PATH

SCRIPTPATH=`dirname $0`
cd $SCRIPTPATH

PROCNAME="maritimecloud-portal-0.1.1.war"

stop () {
	# Find pid
	PID=`cat pid`
	if [ -z $PID ]; then
		echo "MaritimeCloud Portal server not running"
		exit 1
	fi
	echo "Stopping MaritimeCloud Portal server"
	kill $PID
    rm pid
    exit 0
}

case "$1" in
start)
	PID=`cat pid`
	if [ ! -z $PID ]; then
		echo "MaritimeCloud Portal server already running"
        echo started in process with PID=`cat pid` 
		exit 1
	fi
    echo "Starting MaritimeCloud Portal server"
    java -jar $PROCNAME > portal.log 2>&1 &
    echo $! >pid
    echo started in process with PID=`cat pid`
    wait `cat pid` 
    ;;
stop)
    stop
    ;;
restart)
    $0 stop
    sleep 1
    $0 start
    ;;
reploy)
    echo "Downloading $PROCNAME from Cloudbees..."
    wget -O$PROCNAME https://dma.ci.cloudbees.com/view/MaritimeCloud/job/MaritimeCloudPortal/lastSuccessfulBuild/artifact/target/$PROCNAME
    $0 restart
    ;;
*)
    echo "Usage: $0 (start|stop|restart|reploy|help) "
esac
