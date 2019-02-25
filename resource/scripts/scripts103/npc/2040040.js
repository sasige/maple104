var mmm="#fEffect/CharacterEff/1112905/0/1#";
function start() {

    cm.sendSimple ("欢迎来到#r"+cm.GetSN()+"#k,( ^_^ )\r\n\r\n#v4310030##v4310030##v4310030##v4310030##v4310030##v4310030##v4310030##v4310030##v4310030##v4310030##v4310030#\r\n\r\n#r想获得,海量运动会金币吗?\r\n#b请在塔的迷路里寻找奶牛宝宝换取金币吧")
}

function action(mode, type, selection) {
    cm.dispose();

    switch(selection){

        case 1:
            if(cm.haveItem(4031020, 1)) {
                cm.sendOk("您的1个#v4031020#已被收回!为了回报你，我给你#v4310030#!")
                cm.gainItem(4031020, -1);
                cm.gainItem(4310030, 1);
                        cm.serverNotice(5,"[运动会公告]：恭喜运动健将:"+cm.getChar().getName()+" 成功获得一枚运动会金币");
                cm.dispose();
            } else {
                cm.sendOk("#r等您有一个#v4031020#的时候我可以给您换取一枚#v4310030#")
                cm.dispose();    
            };    
            break;

}
}