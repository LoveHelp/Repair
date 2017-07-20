<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';

$userid = $_POST["userid"];
$roleid = $_POST["roleid"];

if($roleid=="2"){
	$sql="select count(1) from bugrepair where state = 0";//待分派（指挥）
	$res = mysql_query($sql);
	$numUnPaiFa = mysql_result($res,0);
	$sql1="select count(1) from bugrepair where ((state > 0 and state < 7) or state = 41 or state = 61) and bugsenduserid = '".$userid."'";//未完工（指挥）
	$res1 = mysql_query($sql1);
	$numUnWanGong = mysql_result($res1,0);
	$sql2="select count(1) from bugrepair where state = 7 and bugsenduserid = '".$userid."'";//已完工
	$res2 = mysql_query($sql2);
	$numWanGong = mysql_result($res2,0);
	$sql3="select count(1) from bugrepair where state = 61 and bugsenduserid = '".$userid."'";//完工待确认
	$res3 = mysql_query($sql3);
	$numUnWanGongSure = mysql_result($res3,0);
	$sql4="select count(1) from bugrepair where type = 1 and state = 5 and bugsenduserid = '".$userid."'";//已核价待维修
	$res4 = mysql_query($sql4);
	$numHeJia = mysql_result($res4,0);
	$sql5="select count(1) from bugrepair where state = 41 and bugsenduserid = '".$userid."'";//4专人待核价；41指挥待核价
	$res5 = mysql_query($sql5);
	$numUnHeJia = mysql_result($res5,0);
	$sql5="select count(1) from bugrepair where state = 1 and bugsenduserid = '".$userid."'";//1指挥中心已分派
	$res5 = mysql_query($sql5);
	$numYiFenpai = mysql_result($res5,0);

	$infoArray=array(
		"numUnPaiFa"=>$numUnPaiFa,
		"numUnWanGong"=>$numUnWanGong,
		"numWanGong"=>$numWanGong,
		"numUnWanGongSure"=>$numUnWanGongSure,
		"numHeJia"=>$numHeJia,
		"numUnHeJia"=>$numUnHeJia,
		"numYiFenpai"=>$numYiFenpai
	);
}else if($roleid=="3"){
	$sql="select count(1) from bugrepair where state = 1 and repairsenduserid = '".$userid."'";//待分派（专人）
	$res = mysql_query($sql);
	$numUnPaiFa = mysql_result($res,0);
	$sql1="select count(1) from bugrepair where ((state > 1 and state < 7) or state = 41 or state = 61) and repairsenduserid = '".$userid."'";//未完工（专人）
	$res1 = mysql_query($sql1);
	$numUnWanGong = mysql_result($res1,0);
	$sql2="select count(1) from bugrepair where state = 7 and repairsenduserid = '".$userid."'";//已完工
	$res2 = mysql_query($sql2);
	$numWanGong = mysql_result($res2,0);
	$sql4="select count(1) from bugrepair where state = 41 and repairsenduserid = '".$userid."'";//专人已核价待指挥核价
	$res4 = mysql_query($sql4);
	$numHeJia = mysql_result($res4,0);
	$sql5="select count(1) from bugrepair where state = 4 and repairsenduserid = '".$userid."'";//专人待核价
	$res5 = mysql_query($sql5);
	$numUnHeJia = mysql_result($res5,0);
	$sql6="select count(1) from bugrepair where type = 1 and state = 5 and repairsenduserid = '".$userid."'";//已核价待维修
	$res6 = mysql_query($sql6);
	$numHeJiaed = mysql_result($res6,0);
	$sql7="select count(1) from bugrepair where state = 6 and repairsenduserid = '".$userid."'";//完工待专人确认
	$res7 = mysql_query($sql7);
	$numUnWanGongZRSure = mysql_result($res7,0);
	$sql8="select count(1) from bugrepair where state = 61 and repairsenduserid = '".$userid."'";//完工待指挥中心确认
	$res8 = mysql_query($sql8);
	$numUnWanGongZHZXSure = mysql_result($res8,0);
	$sql5="select count(1) from bugrepair where state = 2 and repairsenduserid = '".$userid."'";//1甲方专人已分派
	$res5 = mysql_query($sql5);
	$numYiFenpai = mysql_result($res5,0);

	$infoArray=array(
		"numUnPaiFa"=>$numUnPaiFa,
		"numUnWanGong"=>$numUnWanGong,
		"numWanGong"=>$numWanGong,
		"numHeJia"=>$numHeJia,
		"numUnHeJia"=>$numUnHeJia,
		"numHeJiaed"=>$numHeJiaed,
		"numUnWanGongZRSure"=>$numUnWanGongZRSure,
		"numUnWanGongZHZXSure"=>$numUnWanGongZHZXSure,
		"numYiFenpai"=>$numYiFenpai
	);
}else if($roleid=="4"){
	$repairuserid=" and repairuserid REGEXP '^".$userid."$|^".$userid.",|,".$userid.",|,".$userid."$'";
	$sql="select count(1) from bugrepair where state = 2 ".$repairuserid;//待排查
	$res = mysql_query($sql);
	$numUnPaiCha = mysql_result($res,0);
	$sql1="select count(1) from bugrepair where state = 3 ".$repairuserid;//待报价
	$res1 = mysql_query($sql1);
	$numUnBaoJia = mysql_result($res1,0);
	$sql2="select count(1) from bugrepair where state = 5 ".$repairuserid;//待维修
	$res2 = mysql_query($sql2);
	$numUnWeixiu = mysql_result($res2,0);
	
	$sql2="select count(1) from bugrepair where (state = 3 or (state=5 and type=0)) ".$repairuserid;//已排查
	$res2 = mysql_query($sql2);
	$numPaicha = mysql_result($res2,0);
	$sql2="select count(1) from bugrepair where state = 4 ".$repairuserid;//已报价
	$res2 = mysql_query($sql2);
	$numBaojia = mysql_result($res2,0);
	$sql2="select count(1) from bugrepair where state = 6 ".$repairuserid;//已维修
	$res2 = mysql_query($sql2);
	$numWeixiu = mysql_result($res2,0);
	$sql2="select count(1) from bugrepair where state = 7 ".$repairuserid;//已完工
	$res2 = mysql_query($sql2);
	$numWangong = mysql_result($res2,0);
	$sql2="select count(1) from bugrepair where ((state > 1 and state < 7) or state=41 or state=61) ".$repairuserid;//未完工
	$res2 = mysql_query($sql2);
	$numUnWangong = mysql_result($res2,0);

	$infoArray=array(
		"numUnPaiCha"=>$numUnPaiCha,
		"numUnBaoJia"=>$numUnBaoJia,
		"numUnWeixiu"=>$numUnWeixiu,
		"numPaicha"=>$numPaicha,
		"numBaojia"=>$numBaojia,
		"numWeixiu"=>$numWeixiu,
		"numWangong"=>$numWangong,
		"numUnWangong"=>$numUnWangong
	);
}

$result=json_encode($infoArray);
echo $result;
//var_dump($result);

mysql_free_result($res);
mysql_free_result($res1);
mysql_free_result($res2);
mysql_close();

?>
