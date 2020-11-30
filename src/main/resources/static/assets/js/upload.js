layui.use(['upload','layer','table'], function(){
  var $ = layui.jquery
  , upload = layui.upload
  , layer = layui.layer
  , table = layui.table
  
  //拖拽上传mp4
  var uploadInst = upload.render({
    elem: '#select'
    ,url: '/video/upload' //改成您自己的上传接口
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
          content : '<label>翻译语言</label><select id="targetLanguage"><option value="zh">中文</option><option value="en">英语</option><option value="cht">繁体中文</option><option value="fr">法语</option><option value="es">西班牙语</option><option value="ru">俄语</option><option value="ar">阿拉伯语</option> <option value="uy">维吾尔语</option><option value="ti">藏语</option><option value="mo">蒙古语</option><option value="ka">哈萨克语</option><option value="ko">朝鲜语	</option><option value="ii">彝语	</option><option value="za">壮语</option></select><table id="srtTable"></table>',
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
                  url:"/srt/process",
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
                  url:"/srt/translate",
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