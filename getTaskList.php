<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';

$flag = $_POST["flag"];
$roleid = $_POST["roleid"];
$userid=$_POST["userid"];
$orderby=" order by ";
if($flag == "0"){//未完工
	if($roleid=="2"){
		$where="where ((state > 0 and state < 7) or state = 41 or state = 61) and bugsenduserid = '".$userid."'";
	}else if($roleid=="3"){
		$where="where ((state > 1 and state < 7) or state = 41 or state = 61) and repairsenduserid = '".$userid."'";
	}else if($roleid=="4"){
		$where="where state > 1 and state < 7  and repairuserid REGEXP '^".$userid."$|^".$userid.",|,".$userid.",|,".$userid."$'";
	}
	$orderby.="bugfindtime desc";
}else if($flag == "1"){//已完工
	if($roleid=="2"){
		$where="where state = 7 and bugsenduserid = '".$userid."'";
	}else if($roleid=="3"){
		$where="where state = 7 and repairsenduserid = '".$userid."'";
	}else if($roleid=="4"){
		$where="where state = 7 and repairuserid REGEXP '^".$userid."$|^".$userid.",|,".$userid.",|,".$userid."$'";
	}
	$orderby.="compeletchecktime desc";
}else if($flag == "2"){//专人已核价任务列表
	$where="where type = 1 and state = 41 and repairsenduserid = '".$userid."'";
	$orderby.="checktime_zr desc";
}else if($flag == "3"){//已核价任务列表
	if($roleid=="2"){
		$where="where type = 1 and state = 5 and bugsenduserid = '".$userid."'";
	}else if($roleid=="3"){
		$where="where type = 1 and state = 5 and repairsenduserid = '".$userid."'";
	}
	$orderby.="checktime desc";
}else if($flag == "4"){//完工待指挥中心确认任务列表
	$where="where state = 61 and repairsenduserid = '".$userid."'";
	$orderby.="compeletchecktime_zr desc";
}else if($flag == "5"){//已分派
	if($roleid == "2"){
		$where="where state = 1 and bugsenduserid = '".$userid."'";
	}else if($roleid == "3"){
		$where="where state = 2 and repairsenduserid = '".$userid."'";
	}
	$orderby.="bugfindtime desc";
}else{
	$orderby.="bugfindtime desc";
}

$type=isset($_POST["type"])?$_POST["type"]:"88";
if($type != "88"){
	$where.=" and type = ".$type;
}
$bugtype=isset($_POST["bugtype"])?$_POST["bugtype"]:"";
if($bugtype != ""){
	$where.=" and bugtype = '".$bugtype."'";
}

if(isset($_POST["address"]) && $_POST["address"] != ""){
	$address=urldecode($_POST["address"]);
	$where.=" and bugaddr like '%".$address."%'";
}
if(isset($_POST["description"]) && $_POST["description"] != ""){
	$description=urldecode($_POST["description"]);
	$where.=" and bugfinddescrip like '%".$description."%'";
}
//新增筛选条件：发现时间
$startbugfindtime=isset($_POST["startbugfindtime"])?$_POST["startbugfindtime"]:"";
if($startbugfindtime != ""){
	$startbugfindtime=urldecode($startbugfindtime);
	$where.=" and bugfindtime >= '".$startbugfindtime."'";
}
//else{
//	$startbugfindtime=date('y-01-01',time());
//	$where.=" and bugfindtime >= '".$startbugfindtime."'";
//}
if(isset($_POST["endbugfindtime"]) && $_POST["endbugfindtime"] != ""){
	$endbugfindtime=urldecode($_POST["endbugfindtime"]);
	$where.=" and bugfindtime <= '".$endbugfindtime."'";
}
//新增筛选条件：完工确认时间
$startcompletechecktime=isset($_POST["startcompletechecktime"])?$_POST["startcompletechecktime"]:"";
if($startcompletechecktime != ""){
	$startcompletechecktime=urldecode($startcompletechecktime);
	$where.=" and compeletchecktime >= '".$startcompletechecktime."'";
}
//else if($flag == "1"){
//	$startcompletechecktime=date('y-01-01',time());
//	$where.=" and compeletchecktime >= '".$startcompletechecktime."'";
//}
if($startbugfindtime == "" && $startcompletechecktime == ""){
	$startbugfindtime=date('y-01-01',time());
	$where.=" and bugfindtime >= '".$startbugfindtime."'";
}
if(isset($_POST["endcompletechecktime"]) && $_POST["endcompletechecktime"] != ""){
	$endcompletechecktime=urldecode($_POST["endcompletechecktime"]);
	$where.=" and compeletchecktime <= '".$endcompletechecktime."'";
}
$where.=$orderby;
$sql="SELECT * FROM bugrepair ".$where;
//echo $sql;
$q=mysql_query($sql);//bugid,bugaddr,bugfinddescrip,bugfindtime,bugfindphoto,bugaddr
if(mysql_num_rows($q)>0)
{
	while($row=mysql_fetch_assoc($q))
	{
		$output[]=$row; 
	}
	echo json_encode($output); 
}
else {
	echo "0";
}
mysql_free_result($q);

mysql_close();

?>
