<?php
header("Content-type: text/html; charset=utf-8");

//if(!empty($_FILES["file"])){
//	foreach ($_FILES["file"]["error"] as $key => $error) {  
//  $tmp_name = $_FILES["file"]["tmp_name"][$key];  
//  $name    = $_FILES["file"]["name"][$key];
//  $patch="./upload/".$name; 
//  move_uploaded_file($tmp_name, $patch);
//} 
//}
$photo="";
if(!empty($_FILES["file"])){
	$roleid=isset($_POST["roleid"])?$_POST["roleid"]:"0";
	foreach ($_FILES["file"]["error"] as $key => $error) {  
	    $tmp_name = $_FILES["file"]["tmp_name"][$key];  
	    $name    = $_FILES["file"]["name"][$key];
	    //$patch="./upload/".str_replace("ys_","",$name); 
	    $array=explode(".",$name);
	    $houzhui=".".$array[count($array)-1];
	    $time_now=date('YmdHis',time());
	    $new_name=$roleid.$time_now.$key.$houzhui;
	    $photo.=$new_name.",";
	    $patch="./upload/".$new_name; 
	    //move_uploaded_file($tmp_name, $patch);
		move_uploaded_file($tmp_name, iconv("UTF-8", "gb2312", $patch));
	 } 
}
if($photo!=""){
	$photo=substr($photo,0,strlen($photo)-1);
}
//if(!empty($_FILES["file"]))
//{
//foreach ($_FILES["file"]["error"] as $key => $error) {  
//  $tmp_name = $_FILES["file"]["tmp_name"][$key];  
//  $name    = $_FILES["file"]["name"][$key];
//  $path="./upload/".$name; 
//  $path_ls="./upload/ls_".$name; 
//  move_uploaded_file($tmp_name, $path_ls);
//  include_once 'zipImage.php';
//  resizeImage(imagecreatefromjpeg($path_ls),"800","600",$path);
//} 
//}

include_once 'conn.php';

$bugid=$_POST["bugid"];
$flag=$_POST["flag"];

if($flag=="0"){
	$q=mysql_query("SELECT * FROM bugrepair WHERE bugid=".$bugid);
	$result;
	$num_result = mysql_num_rows($q);
	if($num_result==1)
	{
		while($info=mysql_fetch_assoc($q))
		{
			//$result = json_encode($info);
			$rows[]=$info;
		}
		$result=json_encode($rows);
		
		if(is_array($rows)){
			foreach($rows as $row){
				$type=$row['type'];
				$state=$row['state'];
				//$bugtype=$row['bugtype'];
			}
		}
		
				//echo $row['bugtype'];

		if($type == "1" && $state == "4"){
			$sql=mysql_query("select a.partid,price partprice,IF(a.partid <> 0,b.partname,a.partname) AS partname,a.num partnum, IF(a.partid <> 0,b.reference,null) AS reference from offer a LEFT JOIN part b ON a.partid = b.partid and a.partid <> 0 where bugid = ".$bugid);// 
		
			if(mysql_num_rows($sql)>0){
				while($row1=mysql_fetch_assoc($sql)){
					$output[]=$row1; 
				}
				//echo json_encode($output);
				$result .= "+".json_encode($output); 
			}
		}
		echo $result;
	}
}
else {
	$update="";
	$charge=0;
	if($flag=="1"){//添加小故障
		$repairdescrip=urldecode($_POST["repairdescrip"]);
		$repairreason=urldecode($_POST["repairreason"]);
		$repairsolution=urldecode($_POST["repairsolution"]);
		$isonrepair=urldecode($_POST["isonrepair"]);
		$chargeflag=urldecode($_POST["chargeflag"]);
		if($chargeflag=="1"){
//			$bugsendtime=strtotime(urldecode($_POST["bugsendtime"]));//故障发送时间
//			$repairchecktime=strtotime(date("Y-m-d H:i:s"));//
//			$shijian = floor($repairchecktime-$bugsendtime)/60;
//			$con=explode("信号灯不亮",$repairdescrip);
//			//$bugtype=$_POST["bugtype"];
//			if(count($con)>1 && $shijian>30){
//				$ff=1;
//				$charge=180-180*0.3;
//			}else{
//				$ff=2;
//				$charge=180;
//			}
			$charge=180;
		}
		
		$repaircheckphoto=isset($_POST["repaircheckphoto"])?urldecode($_POST["repaircheckphoto"]):"";
		if($repaircheckphoto!=""){
			if($photo==""){
				$photo=$repaircheckphoto;
			}else{
				$photo.=",".$repaircheckphoto;
			}
		}
		
		$update="update bugrepair set repaircheckphoto='".$photo."',repairdescrip='".$repairdescrip."',repairreason='".$repairreason."',repairsolution='".$repairsolution."',repairchecktime=NOW(),type='0',state=5,charge=".$charge.",isonrepair=".$isonrepair." WHERE bugid=".$bugid;
	}else if($flag=="2"){//添加大故障
		$repairdescrip=urldecode($_POST["repairdescrip"]);
		$repairreason=urldecode($_POST["repairreason"]);
		$repairsolution=urldecode($_POST["repairsolution"]);
		$isonrepair=urldecode($_POST["isonrepair"]);
		$chargeflag=urldecode($_POST["chargeflag"]);
		if($chargeflag=="1"){
//			$bugsendtime=strtotime(urldecode($_POST["bugsendtime"]));//故障发送时间
//			$repairchecktime=strtotime(date("Y-m-d H:i:s"));//
//			$shijian = floor($repairchecktime-$bugsendtime)/60;
//			$con=explode("信号灯不亮",$repairdescrip);
//			//$bugtype=$_POST["bugtype"];
//			if(count($con)>1 && $shijian>30){
//				$charge=180-180*0.3;
//			}else{
//				$charge=180;
//			}
			$charge=180;
		}
		
		$repaircheckphoto=isset($_POST["repaircheckphoto"])?urldecode($_POST["repaircheckphoto"]):"";
		if($repaircheckphoto!=""){
			if($photo==""){
				$photo=$repaircheckphoto;
			}else{
				$photo.=",".$repaircheckphoto;
			}
		}
		
		$update="update bugrepair set repaircheckphoto='".$photo."',repairdescrip='".$repairdescrip."',repairreason='".$repairreason."',repairsolution='".$repairsolution."',repairchecktime=NOW(),type='1',state=3,charge=".$charge.",isonrepair=".$isonrepair." WHERE bugid=".$bugid;//,charge=".$charge.",chargetime=NOW()
	}else if($flag=="3"){//维修
		$completephoto=isset($_POST["completephoto"])?urldecode($_POST["completephoto"]):"";
		$completedescrip=urldecode($_POST["completedescrip"]);
		if($completephoto!=""){
			if($photo==""){
				$photo=$completephoto;
			}else{
				$photo.=",".$completephoto;
			}
		}
		$update="update bugrepair set completephoto='".$photo."',completedescrip='".$completedescrip."',completetime=NOW(),state=6 WHERE bugid=".$bugid;
	}
//echo $update;
	$query=mysql_query($update); 

	if($query)
		echo "1"; //成功	
	else 
		echo "0";//失败
}

mysql_close();

?>
