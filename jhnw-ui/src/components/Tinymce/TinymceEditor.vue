<template>
    <div class="tinymce-editor">
      <editor :id="editorId"
              v-model="editorValue"
              :init="editorInit"
              :key="butong"
              :disabled="disabled"
              @onClick="handleClick"/>
    </div>
</template>

<script>
    // 引入组件
    import tinymce from 'tinymce/tinymce';
    import Editor from '@tinymce/tinymce-vue';
    import "tinymce/themes/silver";
    import axios from 'axios'
    // 引入富文本编辑器主题的js和css
    import 'tinymce/themes/silver/theme.min.js';
    import 'tinymce/skins/ui/oxide/skin.min.css';
    import 'tinymce/icons/default';
    import 'tinymce/models/dom';
    // 扩展插件
    import 'tinymce/plugins/image';
    import 'tinymce/plugins/link';
    import 'tinymce/plugins/code';
    import 'tinymce/plugins/table';
    import 'tinymce/plugins/lists';
    import 'tinymce/plugins/wordcount';
    // 字数统计插件

    export default {
        // name: 'TinymceEditor',
        components: { Editor },
        props: {
            niucang:'',
            proId:'',
            id: {
                type: String,
                default: 'tinymceEditor'
            },
            value: {
                type: String,
                default: ''
            },
            disabled: {
                type: Boolean,
                default: false
            },
            plugins: {
                type: [String, Array],
                default: "link lists image code table wordcount"
            },
            toolbar: {
                type: [String, Array],
                default:'bold italic underline strikethrough | fontsizeselect | forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist | outdent indent blockquote | undo redo | link unlink image code | removeformat'
            }
        },
        data() {
            return {
                butong:1,
                xaing:'',
                editorInit: {
                    language_url: '/tinymce/langs/zh_CN.js',
                    language: 'zh_CN',
                    skin_url: '/tinymce/skins/ui/oxide',
                    height: 300,
                    plugins: this.plugins,
                    toolbar: this.toolbar,
                    statusbar: true, // 底部的状态栏
                    menubar: true, // 最上方的菜单
                    branding: false, // 水印“Powered by TinyMCE”
                    images_upload_handler: (blobInfo, success, failure) => {
                        this.$emit('handleImgUpload', blobInfo, success, failure)
                    }
                },
                editorId: this.id,
                editorValue: this.value,
                content:{
                    // html:'<p>啊实打实大所</p>'
                },
            }
        },
        watch: {
            editorValue(newValue) {
                this.$emit('input', newValue)
            }
        },
        activated(){
          this.butong++
        },
        mounted() {
            tinymce.init({})
        },
        methods: {
            //给父组件文本内容
            geifu(){
              return this.editorValue
            },
            // https://github.com/tinymce/tinymce-vue => All available events

            handleClick(e) {
                this.$emit('onClick', e, tinymce)
            },
            clear() {
                thiseditorValue = ''

            }
        }
    }
</script>
