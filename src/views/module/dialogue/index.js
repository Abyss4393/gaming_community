export var QuickTextLists = [
    { text: '这里是jwchat，您想了解什么问题。' },
    { text: 'jwchat是最好的聊天组件' },
    { text: '道可道，非常道。名可名，非常名。' },
    { text: '谁将烟焚散，散了纵横的牵绊；听弦断，断那三千痴缠。' },
    { text: '长夏逝去。山野间的初秋悄然涉足。' },
    { text: '江南风骨，天水成碧，天教心愿与身违。' },
    { text: '总在不经意的年生。回首彼岸。纵然发现光景绵长。' },
    { text: '外面的烟花奋力的燃着，屋里的人激情的说着情话' },
    { text: '假如你是云，我就是雨，一生相伴，风风雨雨；' },
    { text: '即使泪水在眼中打转，我依旧可以笑的很美，这是你学不来的坚强。' },
    { text: ' 因为不知来生来世会不会遇到你，所以今生今世我会加倍爱你。' },
]

export var ToolBarConfig = {
    // 现在只配置了 ["file", "video", "img", "hongbao", "more", "history"]
    show: ['file', 'history', 'img', 'video', ['文件1', '', '美图']],// 二级数组中放自定义名称
    // 是否显示表情图标
    showEmoji: true,
    /**
    * @description: 点击按钮的回调函数
    * @param {*} type 当前点击的按钮
    * @param {*} plyload 附加文件或者需要处理的数据
    * @return {*}
    */
    callback: (type, plyload) => {
        console.log(type, plyload);
    }
}