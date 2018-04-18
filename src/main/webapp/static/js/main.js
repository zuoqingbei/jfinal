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

for (var i = 1; i <= 10; i++) {
    barCodeList.push({
        imgUrl: "img/barCode.png",
        x1: 360,
        y1: 123,
        x2: 1020,
        y2: 860,
        sn: "004365896312552",
        orderNumber: "P2750436589637682452",
        position: "C123456",
        description: "冰箱-BCD-251WDCPU1",
        number: "" + i + i
    })
}
;


//入口
$(function () {

    //日期和时间
    clock();

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
        var confirm = window.confirm("请确认无人机周围环境，是否能安全起飞？");
        if (confirm === true) {
        	startFly();
            $(".start>.text").html("正在<br>盘点").siblings(".btn").addClass("working");
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
    $(".start>.text").html("开始<br>盘点").siblings(".btn").removeClass("working");
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
    var htmlInner = '<img src="/file/'+data.path+"/"+data.fullName+'" alt="">\n' +
        '<p class="coordinates">坐标：<span class="x1">(' + data.leftTopLat + '，</span><span class="y1">' + data.leftTopLon + ') (</span><span class="x2">' + data.width + '，</span><span class="y2">' + data.height + ')</span></p>\n' +
        '<p>SN：&nbsp;&nbsp;<span class="sn">' + data.key + '</span></p>\n' +
        '<p>单号：<span class="orderNumber"></span></p>\n' +
        '<p>库位：<span class="position"></span></p>\n' +
        '<p>描述：<span class="description"></span></p>\n' +
        '<p>数量：<span class="number"></span></p>';
    $resultList.find(".currentR1").html(htmlInner);

    $($("li[class*=current]").toArray().reverse()).each(function () {
        $(this).attr("class", $(this).prev().attr("class"))
    });

    var $l3 = $(".currentL3:eq(0)").remove();
    $l3.attr("class", "item bdAndBg currentR3 absCenter");
    $(".resultList ul").append($l3);

    $(".resultList>h3>.number>span").text(++barCodeCounter);
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
	if(parseFloat(height)<0){
		height=0;
	};
	if(parseFloat(speed)<0){
		speed=0;
	};
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
        return battery ? battery  + "%" : null
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





