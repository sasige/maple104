/*
Multi-Purpose NPC
Author: Moogra
 */
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.sendOk("#e好的，如果你需要我帮助随时可以找我.#k");
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendOk("#e好的，你想知道什么请随时来问我..#k");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            cm.sendSimple ("#e你好? #e#r[#h #]#k#n ,#e我是#e#b任务发布NPC#k#n,#e现在为止."+cm.GetSN()+"的任务有#k#n\r\n\r\n#e#b请选择:#k#n\r\n\r\n#e#r#L5#[装]弓手任务#l \r\n\r\n#L6#[装]法师任务#l \r\n\r\n#L7#[装]飞侠任务#l \r\n\r\n#L0#[装]战士归来#l \r\n\r\n#L1#[装]基德门徒#l \r\n\r\n#L2#[装]元素魔法#l#k#n\r\n\r\n#e#r#L3#[装]暗藏的狙击手#l \r\n\r\n#L4#[装]异界力量突起#l\r\n\r\n#L8#  #g更多任务....敬请期待...#l#k#n");
        } else if (status == 1) {
            switch(selection) {
                case 0:
                    cm.openNpc(9000007);
                    break;
                case 1:
                    cm.openNpc(2111001);
                    break;
                case 2:
                    cm.openNpc(2111005);
                    break;
                case 3:
                    cm.openNpc(2111008);
                    break;
                case 4:
                    cm.openNpc(2110001);
                    break;
                case 5:
                    cm.openNpc(9000011);
                    break;
                case 6:
                    cm.openNpc(2040046);
                    break;
                case 7:
                    cm.openNpc(1052012);
                    break;
                case 8:
                    cm.openNpc(9310070);
                    break;
            }
        }
    }
}
