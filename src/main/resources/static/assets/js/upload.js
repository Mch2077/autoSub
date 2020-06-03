layui.use(['upload','layer','table'], function(){
  var $ = layui.jquery
  , upload = layui.upload
  , layer = layui.layer
  , table = layui.table
  
  //拖拽上传mp4
  var uploadInst = upload.render({
    elem: '#select'
    ,url: 'http://192.168.0.107:8080/video/upload' //改成您自己的上传接口
    ,accept: 'video' //视频
    ,exts: 'mp4' //只允许上传mp4
    ,auto: false
    ,headers: {Authorization: $.session.get("token")}
    //,multiple: true
    ,bindAction: '#upload'
    ,size: 512000 //限制文件大小，单位 KB
    ,data: {
      language: function(){
          return $('#language').val();
      }
    }
    ,before: function(obj){
      layer.msg('uploading...', {
      icon: 16,
      shade: 0.01,
      time: 0
      })
    }
    ,done: function(res){
      if (res.code == 0) {
        layer.close(layer.msg());
        layer.msg('upload success');
        //$.session.set("videoUrl", res.videoUrl);
        $.session.set("token", res.token);
        console.log(res);
        //页面层
        layer.open({
          type: 1, //1
          title : "字幕编辑",
          skin: 'layui-layer-rim', //加上边框
          area: ['90%', '85%'], //宽高
          content : '<label>翻译语言</label><select id="targetLanguage"><option value="zh">中文</option><option value="en">英语</option><option value="yue">粤语</option><option value="wyw">文言文</option><option value="jp">日语</option><option value="kor">韩语</option><option value="fra">法语</option><option value="spa">西班牙语</option><option value="th">泰语</option><option value="ara">阿拉伯语</option><option value="ru">俄语</option><option value="pt">葡萄牙语</option><option value="de">德语</option><option value="it">意大利语</option><option value="el">希腊语</option><option value="nl">荷兰语</option><option value="pl">波兰语</option><option value="bul">保加利亚语</option><option value="est">爱沙尼亚语</option><option value="dan">丹麦语</option><option value="fin">芬兰语</option><option value="cs">捷克语</option><option value="rom">罗马尼亚语</option><option value="slo">斯洛文尼亚语</option><option value="swe">瑞典语</option><option value="hu">匈牙利语</option><option value="cht">繁体中文</option><option value="vie">越南语</option></select><table id="srtTable"></table>',
          btn : [ '提交', '关闭', '翻译' ],

          success : function(index, layero) {

                  table.render({
                    elem: '#srtTable'
                    ,width:'100%'
                    ,data: res.data
                    ,limit: Number.MAX_VALUE
                    ,cols: [[ //标题栏
                      {field: 'sn', title: '#', width: '5%', sort: true}
                      ,{field: 'bg', width: '20%', title: '开始时间'}
                      ,{field: 'ed', width: '20%', title: '结束时间'}
                      ,{field: 'onebest', width: '55%', title: '内容', edit: 'text'}
                      ]]
                  });
            },
//$.session.get("videoUrl")
            yes : function(index, layero) {
                var data = table.cache['srtTable'];
                $.ajax({
                  cache: false,
                  type: "post",
                  url:"http://192.168.0.107:8080/srt/process",
                  data: {videoUrl: res.videoUrl,
                          srt: JSON.stringify(data)},
                  dataType: 'json',
                  headers: {Authorization: $.session.get("token")}, 
                  async: true,
                  beforeSend:function(XMLHttpRequest){ 
                    layer.msg('processing...', {
                      icon: 16,
                      shade: 0.01,
                      time: 0
                      })
                  }, 
                      success : function(result) {
                        if (result.code==200) {   
                            //layer.msg("login success");
                            layer.close(layer.msg());
                            $.session.set("webShowPath", "/res/"+result.webShowPath);
                            $.session.set("token", result.token);
                            window.location.href = 'preview.html';
                        }else{
                          layer.msg(result.msg);
                        }
                      },
                      error : function(errorMsg) {
                          //请求失败时执行该函数
                          layer.msg("server error");
                      }
                    });
                },

            btn3 : function(index, layero) {
                var data = table.cache['srtTable'];
                var targetLanguage = $('#targetLanguage').val();

                $.ajax({
                  cache: false,
                  type: "post",
                  url:"http://192.168.0.107:8080/srt/translate",
                  data: {srt: JSON.stringify(data),
                          targetLanguage: targetLanguage},
                  dataType: 'json',
                  headers: {Authorization: $.session.get("token")}, 
                  async: true,
                  beforeSend:function(XMLHttpRequest){ 
                    layer.msg('translating...', {
                      icon: 16,
                      shade: 0.01,
                      time: 0
                      })
                  }, 
                      success : function(result) {
                        if (result.code==0) {   
                            //layer.msg("login success");
                            layer.close(layer.msg());
                            table.render({
                              elem: '#srtTable'
                              ,width:'100%'
                              ,data: result.data
                              ,limit: Number.MAX_VALUE
                              ,cols: [[ //标题栏
                                {field: 'sn', title: '#', width: '5%', sort: true}
                                ,{field: 'bg', width: '20%', title: '开始时间'}
                                ,{field: 'ed', width: '20%', title: '结束时间'}
                                ,{field: 'onebest', width: '55%', title: '内容', edit: 'text'}
                                ]]
                            });
                        }else{
                          layer.msg(result.msg);
                        }
                      },
                      error : function(errorMsg) {
                          //请求失败时执行该函数
                          layer.msg("server error");
                      }
                    });
                  return false;
                }
        });
        //window.location.href = 'preview.html';
      }else{
          layer.msg(res.msg);
      }

    }
    ,error: function(){
        layer.msg('upload failed');
    }
  });
});