<?php
header("Content-type: text/html; charset=utf-8");
include_once 'conn.php';

$repairuserid = "4";//$_POST["repairuserid"];
$state = isset($_POST["state"])?$_POST["state"]:"2";
$upd = isset($_POST["upd"])?$_POST["upd"]:"1";
//select * from bugrepair where repairuserid REGEXP '^1$|^1,|,1,|,1$' 
if($upd=="1"){//排查或报价或维修提交后的修改
	switch($state){
		case "2":
			$where=" where (state=3 or (state = 5 and type = 0))";
			break;
		case "3":
			$where=" where state=4";
			break;
		case "5":
			$where=" where state=6";
			break;
		default:
			$where=" where state=100";
			break;
	}
	$where.=" and repairuserid REGEXP '^".$repairuserid."$|^".$repairuserid.",|,".$repairuserid.",|,".$repairuserid."$'";
}else{
	$where="where state = ".$state." and repairuserid REGEXP '^".$repairuserid."$|^".$repairuserid.",|,".$repairuserid.",|,".$repairuserid."$'";
}

$orderby=" order by ";
switch($state){
	case "2":
		$orderby.="repairsendtime desc";
		break;
	case "3":
		$orderby.="repairchecktime desc";
		break;
	case "5":
		$orderby.="checktime desc,repairchecktime desc";
		break;
	default:
		$orderby.="repairsendtime desc";
		break;
}
$where.=$orderby;
//echo $where;
$q=mysql_query("SELECT * FROM bugrepair ".$where);//bugid,bugaddr,bugfinddescrip,bugfindtime,bugfindphoto,bugaddr

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
