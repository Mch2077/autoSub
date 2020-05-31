    layui.use(['upload','layer','table'], function(){
      var $ = layui.jquery
      , upload = layui.upload
      , layer = layui.layer
      , table = layui.table;

      
      //拖拽上传mp4
      var uploadInst = upload.render({
        elem: '#select'
        ,url: 'http://localhost:8080/video/upload' //改成您自己的上传接口
        ,accept: 'video' //视频
        ,exts: 'mp4' //只允许上传mp4
        ,auto: false
        ,headers: {Authorization: $.session.get("token")}
        //,multiple: true
        ,bindAction: '#upload'
        ,size: 512000 //限制文件大小，单位 KB
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
            $.session.set("videoUrl", res.videoUrl);
            $.session.set("token", res.token);
            console.log(res);
            //页面层
            layer.open({
              type: 1,
              title : "字幕编辑",
              skin: 'layui-layer-rim', //加上边框
              area: ['80%', '60%'], //宽高
              content : '<table id="templateTable"></table>',
              btn : [ '提交', '关闭' ],

              success : function(index, layero) {

                      table.render({
                        elem: '#templateTable'
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

                yes : function(index, layero) {
                    var data = table.cache['templateTable'];
                    $.ajax({
                      cache: false,
                      type: "post",
                      url:"http://localhost:8080/srt/process",
                      data: {videoUrl: $.session.get("videoUrl"),
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