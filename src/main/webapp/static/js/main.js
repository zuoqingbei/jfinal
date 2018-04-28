var debug = false,
    allVideoAddr1 = "http://192.168.1.102:10800/play.html?channel=1",
    allVideoAddr2 = "http://192.168.1.102:10800/play.html?channel=1",
    barCodeCounter = 0,
    startBtnStatus = true,//开始按钮是否能点击
    takePhotoSound = $("audio")[0],
    $uavVideoReplace = $(".uavVideoReplace>video"),
    $speedBox = $("#speedBox"),
    $heightBox = $("#heightBox"),
    uavSpeed = 0.2,
    uavSpeedMin = 0,
    uavSpeedMax = 1,
    uavHeight = 2.5,
    uavHeightMin = 0,
    uavHeightMax = 3.5,
    uavBattery = 0.63,
    uavStatusCounter = null,
    $resultList = $('.resultList'),
    $deviceList = $('.deviceList'),
    barCodeList = [];
	codeInfoMap=new Map();

var data01 = {
        // orderNumber: "P2756552896376043824",
        // position: "C123456",
        description: "冰箱-BCD-251WDCPU1",
        number: 1
    },
    data02 = {
        description: "海尔凌越S4-X",
        number: 2
    },
    data03 = {
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 4
    },
    data04 = {
        description: "TAB-T750B智能扫地机器人玛奇朵M3",
        number: 8
    },
    datas = [
        data01, data02, data03, data04, data02, data03, data01, data04, data01, data03, data02, data04, data03, data01
    ];
    for(var i=0;i<datas.length;i++){
        datas[i].orderNumber = "P"+(2756552896376043824+i);
        datas[i].orderNumber = "C"+(123456+i);
    }

//入口
$(function () {

    /*	$(".uavVideoReplace>video").click(function(){
            $(this)[0].play();
        })*/

    //日期和时间
    clock();
    
    initCodeMap();
    
    //选择无人机
    chooseUav();

    //仓库内监控切换
    // allVideoSwitch();

    //页面加载完后开始播放视频
    $(".uavVideoReplace>video")[0].play();

    //点击开始按钮
    //uavStart();
    $(".start").click(function () {
        uavStart();
    });
    // 监控自动运行(不成功)
    setTimeout(function () {
        $(".allVideo iframe").click()
    }, 2000);

});

//开始
function uavStart() {
    if (!$(".deviceList li.active").length) {
        alert("请选择无人机");
        return;
    }
    if (startBtnStatus) {
        //var confirm = window.confirm("请确认无人机周围环境，是否能安全起飞？");
        //if (confirm === true) {
    	if(true) {
            startFly();
            $(".start>.text").html("正在<br>盘库").siblings(".btn").addClass("working");
            /* $(".start>.text").unbind().click(function () {
                 uavStop();
             });*/
            startBtnStatus = false;
            $(".deviceList").find("li.active").addClass("uavRotate");
            $uavVideoReplace[0].pause();//视频切换到实时图像
            $uavVideoReplace.parent().hide().siblings().show();
            $resultList.find("ul>li").html("");
            $deviceList.find("li.active .i-battery").removeClass("warning");

            //条码列表中的模拟动画
            // scanBarcode();

            //无人机实时状态
            if (debug) {
                var speed = 0, height = 0, battery = 0.63;
                uavStatusCounter = setInterval(function () {
                    if (speed < 0.2) {
                        speed += rdm(0.02, 0.04)
                    } else {
                        speed += rdm(-0.02, 0.02)
                    }
                    if (height < 2.3) {
                        height += rdm(0.15)
                    } else {
                        height += rdm(-0.1, 0.1)
                    }
                    if (battery < 0.1) {
                        $deviceList.find("li.active .i-battery").addClass("warning")
                    } else {
                        battery -= rdm(0.008)
                    }
                    uavStatus(battery, speed.toFixed(2), height.toFixed(2));
                }, 100)
            }
        }
    }
}

//停止
function uavStop() {
    $(".start>.text").html("开始<br>盘库").siblings(".btn").removeClass("working");
    /* $(".start>.text").unbind().click(function () {
         uavStart();
     });*/
    startBtnStatus = true;
    $(".deviceList").find("li.active").removeClass("uavRotate");
    $uavVideoReplace.parent().show().siblings().hide();
    $uavVideoReplace[0].play();
    barCodeCounter = 0;
    clearInterval(uavStatusCounter);
    uavStatus(null, 0, 0);
}

//扫描条码并记录
function scanning(data) {
    if (!data) {
        return;
    }
    takePhotoSound.play();
    /*
        <li class="item bdAndBg currentL3 absCenter">
            <img src="img/barCode.png" alt="">
            <p class="coordinates">坐标：<span class="x1">36.123654，</span><span class="y1">120.265422</span></p>
        <p>SN：&nbsp;&nbsp;<span class="sn">004365896312552</span></p>
        <p>单号：<span class="orderNumber">P2750436589637682452</span></p>
        <p>库位：<span class="position">C123456</span></p>
        <p>描述：<span class="description">冰箱-BCD-251WDCPU1</span></p>
        <p>数量：<span class="number">23423</span></p>
        </li>
    */
    var codeInfo=codeInfoMap.get(data.key);
    //console.log(data.key,codeInfo);
    var htmlInner = '<img src="/file/' + data.path + "/" + data.fullName + '" alt="">\n' +
        '<p class="coordinates">坐标：<span class="x1">(' + data.leftTopLat + '，</span><span class="y1">' + data.leftTopLon + ') (</span><span class="x2">' + data.width + '，</span><span class="y2">' + data.height + ')</span></p>\n' +
        '<p>SN：&nbsp;&nbsp;<span class="sn">' + data.key + '</span></p>\n' +
        '<p>单号：<span class="orderNumber">' + formatOutput(codeInfo.orderNumber) + '</span></p>\n' +
        '<p>库位：<span class="position">' + formatOutput(codeInfo.position) + '</span></p>\n' +
        '<p>描述：<span class="description">' + formatOutput(codeInfo.description)+ '</span></p>\n' +
        '<p>层数：<span class="number">' + formatOutput(codeInfo.number) + '</span></p>';
    $resultList.find(".currentR1").html(htmlInner);

    $($("li[class*=current]").toArray().reverse()).each(function () {
        $(this).attr("class", $(this).prev().attr("class"))
    });

    var $l3 = $(".currentL3:eq(0)").remove();
    $l3.attr("class", "item bdAndBg currentR3 absCenter");
    $(".resultList ul").append($l3);

    $(".resultList>h3>.number>span").text(++barCodeCounter);//计数
    $resultList.find(".currentR1").html("");
}

//模拟扫描
function scanBarcode() {
    if (debug) {
        setTimeout(function () {
            var data = barCodeList[barCodeCounter];
            if (data) {
                scanning(data);//扫描条码并记录
                scanBarcode();
            } else {
                uavStop();
            }
        }, (Math.random() + 1) * 1000) // 变速
    } else {
        var data = {};//自定义
        //scanning(data);//接收扫描数据
    }
}

//选择无人机
function chooseUav() {
    $(".deviceList li").click(function () {
        if (startBtnStatus) {
            if ($(this).index() > 0) {//只点第一个
                return false;
            }
            if (!$(this).hasClass("active")) {
                $(this).addClass("active").siblings().removeClass("active")
            } else {
                $(this).removeClass("active")
            }
        }
    });


}

//无人机实时状态
function uavStatus(battery, speed, height) {
    if (parseFloat(height) < 0) {
        height = 0;
    }
    ;
    if (parseFloat(speed) < 0) {
        speed = 0;
    }
    ;
    if (speed) {
        $speedBox.find(".fill").addClass("value")
    } else {
        $speedBox.find(".fill").removeClass("value")
    }
    $speedBox.find(".number").text(speed).end().find(".progressbar>.fill").css("width", speed / (uavSpeedMax - uavSpeedMin) * 100 + "%");

    if (height) {
        $heightBox.find(".fill").addClass("value")
    } else {
        $heightBox.find(".fill").removeClass("value")
    }
    $heightBox.find(".number").text(height).end().find(".progressbar>.fill").css("width", height / (uavHeightMax - uavHeightMin) * 100 + "%");
    $deviceList.find("li.active .fill").css("width", function () {
        return battery ? battery + "%" : null
    });
}

function allVideoSwitch() {
    setInterval(function () {
        var flag = true,
            allVideoAddr = "";
        if (flag) {
            allVideoAddr = allVideoAddr1;
            flag = false;
        } else {
            allVideoAddr = allVideoAddr2;
            flag = true;
        }
        $(".allVideo>iframe").attr("src", allVideoAddr)
    }, 15000)
}

//日期和时间
function clock() {
    setInterval(function () {
        var date = new Date();
        $(".date").text(date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate());
        /*这里不能倒着截，因为pc和移动端字符串不一样*/
        $(".time").text(date.toString().substr(16, 8));
    }, 1000);
}

//调整窗口大小
function windowResize() {
    var window_W = $(window).width();
    var window_H = $(window).height();
    // alert(window_W + " x " + window_H)
    var ratio = window_W / window_H;
    var $htmlBody = $("html,body");
    if (ratio > 1.6) {
        $htmlBody.height(window_W / 1.6).width(window_W);
        $("html").css("font-size", window_W / 1920 * 16 + "px");
    } else {
        $htmlBody.width(window_H * 1.6).height(window_H);
        $("html").css("font-size", window_H / 1200 * 16 + "px");
    }
    console.log(window_W, window_H, $("html").css("font-size"), ratio)
    // $("html").css("font-size", "8px");
}

/*自定义工具*/
function rdm(a, b) {
    if (arguments.length === 2) {
        return Math.random() * (b - a) + a
    }
    return Math.random() * a
}

function uavVideo() {
    // var player = videojs('my-player');
    var options = {};
    var player = videojs('my-player', options, function onPlayerReady() {
        videojs.log('Your player is ready!');
        // In this context, `this` is the player that was created by Video.js.
        this.play();
        // How about an event listener?
        this.on('ended', function () {
            videojs.log('Awww...over so soon?!');
        });
    });
}
//组装条码数据
function initCodeMap(){
	codeInfoMap=new Map();
	var dataArr=[];
	/*dataArr.push({
		sn:"130180402965",
	    orderNumber:"单号01",
	    position:"库位01",
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 1
    });
	dataArr.push({
		sn:"130180402966",
	    orderNumber:"单号02",
	    position:"库位02",
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 1
    });
	dataArr.push({
		sn:"130180402967",
	    orderNumber:"单号03",
	    position:"库位03",
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 1
    });
	dataArr.push({
		sn:"130180402968",
	    orderNumber:"单号04",
	    position:"库位04",
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 1
    });
	
	dataArr.push({
		sn:"130180402969",
	    orderNumber:"单号05",
	    position:"库位05",
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 1
    });
	dataArr.push({
		sn:"130180402970",
	    orderNumber:"单号07",
	    position:"库位07",
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 1
    });
	dataArr.push( {
		sn:"130180402971",
	    orderNumber:"单号08",
	    position:"库位08",
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 1
    });
	dataArr.push({
		sn:"130180402972",
	    orderNumber:"单号09",
	    position:"库位09",
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 1
    });
	dataArr.push({
		sn:"130180402973",
	    orderNumber:"单号10",
	    position:"库位10",
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 1
    });
	dataArr.push({
		sn:"130180402974",
	    orderNumber:"单号10",
	    position:"库位10",
        description: "海尔23.6寸智能魔镜M3S-23IN",
        number: 1
    });
	
	//第二层
		dataArr.push({
			sn:"289087689013",
		    orderNumber:"单号01",
		    position:"库位01",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 2
	    });
		dataArr.push({
			sn:"289087689014",
		    orderNumber:"单号02",
		    position:"库位02",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 2
	    });
		dataArr.push({
			sn:"289087689015",
		    orderNumber:"单号04",
		    position:"库位04",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 2
	    });
		
		dataArr.push({
			sn:"289087689016",
		    orderNumber:"单号05",
		    position:"库位05",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 2
	    });
		dataArr.push({
			sn:"289087689017",
		    orderNumber:"单号06",
		    position:"库位06",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 2
	    });
		dataArr.push({
			sn:"289087689018",
		    orderNumber:"单号07",
		    position:"库位07",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 2
	    });
		dataArr.push({
			sn:"289087689019",
		    orderNumber:"单号08",
		    position:"库位08",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 2
	    });
		dataArr.push({
			sn:"289087689020",
		    orderNumber:"单号09",
		    position:"库位09",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 2
	    });
		dataArr.push({
			sn:"289087689021",
		    orderNumber:"单号10",
		    position:"库位10",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 2
	    });
		dataArr.push({
			sn:"289087689022",
		    orderNumber:"单号10",
		    position:"库位10",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 2
	    });
		//第三层
		
		dataArr.push({
			sn:"389076590091",
		    orderNumber:"单号01",
		    position:"库位01",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 3
	    });
		dataArr.push({
			sn:"389076590092",
		    orderNumber:"单号02",
		    position:"库位02",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 3
	    });
		dataArr.push({
			sn:"389076590093",
		    orderNumber:"单号03",
		    position:"库位03",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 3
	    });
		dataArr.push({
			sn:"389076590094",
		    orderNumber:"单号04",
		    position:"库位04",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 3
	    });
		
		dataArr.push({
			sn:"389076590095",
		    orderNumber:"单号05",
		    position:"库位05",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 3
	    });
		dataArr.push({
			sn:"389076590096",
		    orderNumber:"单号06",
		    position:"库位06",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 3
	    });
		dataArr.push({
			sn:"389076590097",
		    orderNumber:"单号07",
		    position:"库位07",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 3
	    });
		dataArr.push({
			sn:"389076590098",
		    orderNumber:"单号08",
		    position:"库位08",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 3
	    });
		dataArr.push({
			sn:"389076590099",
		    orderNumber:"单号09",
		    position:"库位09",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 3
	    });
		dataArr.push({
			sn:"389076590100",
		    orderNumber:"单号10",
		    position:"库位10",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 3
	    });*/
		
		//第四层
		
		dataArr.push({
			sn:"489000019001",
		    orderNumber:"单号01",
		    position:"库位01",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 4
	    });
		dataArr.push({
			sn:"489000019002",
		    orderNumber:"单号02",
		    position:"库位02",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 4
	    });
		dataArr.push({
			sn:"489000019003",
		    orderNumber:"单号03",
		    position:"库位03",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 4
	    });
		dataArr.push({
			sn:"489000019004",
		    orderNumber:"单号04",
		    position:"库位04",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 4
	    });
		
		dataArr.push({
			sn:"489000019005",
		    orderNumber:"单号05",
		    position:"库位05",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 4
	    });
		dataArr.push({
			sn:"489000019006",
		    orderNumber:"单号06",
		    position:"库位06",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 4
	    });
		dataArr.push({
			sn:"489000019007",
		    orderNumber:"单号07",
		    position:"库位07",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 4
	    });
		dataArr.push({
			sn:"489000019008",
		    orderNumber:"单号08",
		    position:"库位08",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 4
	    });
		dataArr.push({
			sn:"489000019009",
		    orderNumber:"单号09",
		    position:"库位09",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 4
	    });
		dataArr.push({
			sn:"489000019010",
		    orderNumber:"单号10",
		    position:"库位10",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 4
	    });
		

		//第五层
	/*	
		dataArr.push({
			sn:"567901232409",
		    orderNumber:"单号01",
		    position:"库位01",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 5
	    });
		dataArr.push({
			sn:"567901232410",
		    orderNumber:"单号02",
		    position:"库位02",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 5
	    });
		dataArr.push({
			sn:"567901232411",
		    orderNumber:"单号03",
		    position:"库位03",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 5
	    });
		dataArr.push({
			sn:"567901232412",
		    orderNumber:"单号04",
		    position:"库位04",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 5
	    });
		
		dataArr.push({
			sn:"567901232413",
		    orderNumber:"单号05",
		    position:"库位05",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 5
	    });
		dataArr.push({
			sn:"567901232414",
		    orderNumber:"单号06",
		    position:"库位06",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 5
	    });
		dataArr.push({
			sn:"567901232415",
		    orderNumber:"单号07",
		    position:"库位07",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 5
	    });
		dataArr.push({
			sn:"567901232416",
		    orderNumber:"单号08",
		    position:"库位08",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 5
	    });
		dataArr.push({
			sn:"567901232417",
		    orderNumber:"单号09",
		    position:"库位09",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 5
	    });
		dataArr.push({
			sn:"567901232418",
		    orderNumber:"单号10",
		    position:"库位10",
	        description: "海尔23.6寸智能魔镜M3S-23IN",
	        number: 5
	    });*/
	
	for(var x=0;x<dataArr.length;x++){
		codeInfoMap.put(dataArr[x].sn,dataArr[x]);
	}
	
}
function formatOutput(str) {
    if(str==null||str==undefined||str=='null'||str==''){
        return '';
    }else
        return str;
}
/** 
 *  
 * 描述：js实现的map方法 
 * @returns {Map} 
 */  
function Map(){  
 var struct = function(key, value) {  
  this.key = key;  
  this.value = value;  
 };  
// 添加map键值对  
 var put = function(key, value){  
   for (var i = 0; i < this.arr.length; i++) {  
   if ( this.arr[i].key === key ) {  
    this.arr[i].value = value;  
    return;  
   }  
  };  
  this.arr[this.arr.length] = new struct(key, value);  
 };  
//  根据key获取value   
 var get = function(key) {  
  for (var i = 0; i < this.arr.length; i++) {  
   if ( this.arr[i].key === key ) {  
    return this.arr[i].value;  
   }  
  }  
 return null;  
 };  
//   根据key删除  
 var remove = function(key) {  
  var v;  
  for (var i = 0; i < this.arr.length; i++) {  
   v = this.arr.pop();  
   if ( v.key === key ) {  
    continue;  
   }  
   this.arr.unshift(v);  
  }  
 };  
//   获取map键值对个数  
 var size = function() {  
  return this.arr.length;  
 };  
// 判断map是否为空    
 var isEmpty = function() {  
  return this.arr.length <= 0;  
 };  
 this.arr = new Array();  
 this.get = get;  
 this.put = put;  
 this.remove = remove;  
 this.size = size;  
 this.isEmpty = isEmpty;  
}  




