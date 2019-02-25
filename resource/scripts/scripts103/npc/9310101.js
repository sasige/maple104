

var status = 0;
var zz =" 小七冒险岛"; //这里设置名字
var yb = 5; //元宝
var ttt ="#fUI/Basic/CheckBox/3#";//"+ttt+"//美化
var temp = "#fEtc/pachinko/controller/base/1#";
var temp2 = "#fEtc/SpeedAnimationQuiz/BeijingOlympic/AniQuiz/5/ani/1#";
function start() {
status = -1;
action(1, 0, 0);
}

function action(mode, type, selection) {

if (mode == -1) {
cm.dispose();

} else {

if (mode == 0 && status == 0) {
cm.dispose();
return;
}

if (mode == 1)
status++;
else
status--;

if (status == 0) {

if (cm.getPlayer().getnld() >= 0) {

var text ="\r\n#b#L1##r"+mmm+"换购温度计#k(5元宝)#n#l ";
text +="\r\n#b#L2##r"+mmm+"换购火焰刀#k(5元宝)#n#l ";
text +="\r\n#b#L3##r"+mmm+"换购蔓藤鞭#k(5元宝)#n#l ";
text +="\r\n#b#L4##r"+mmm+"换购圣贤仗#k(5元宝)#n#l ";
text +="\r\n#b#L5##r"+mmm+"换购七夕饰#k(5元宝)#n#l ";
text +="\r\n#b#L6##r"+mmm+"换购光线鞭#k(5元宝)#n#l ";
text +="\r\n#b#L7##r"+mmm+"换购鱼戒指#k(5元宝)#n#l ";
text +="\r\n#b#L8##r"+mmm+"换购圣诞树#k(5元宝)#n#l ";
text +="\r\n#b#L9##r"+mmm+"换购白日剑#k(5元宝)#n#l ";
text +="\r\n#b#L10##r"+mmm+"换购领路灯#k(5元宝)#n#l ";
text +="\r\n#b#L11##r"+mmm+"换购南瓜灯#k(5元宝)#n#l ";
text +="\r\n#b#L12##r"+mmm+"换购大将旗#k(5元宝)#n#l ";
text +="\r\n#b#L13##r"+mmm+"换购乌龙茶#k(5元宝)#n#l ";
text +="\r\n#b#L14##r"+mmm+"换购调色板#k(5元宝)#n#l ";
text +="\r\n#b#L15##r"+mmm+"换购霓虹灯#k(5元宝)#n#l ";
text +="\r\n#b#L16##r"+mmm+"换购樱花伞#k(5元宝)#n#l ";
text +="\r\n#b#L17##r"+mmm+"换购中国结#k(5元宝)#n#l ";
text +="\r\n#b#L18##r"+mmm+"换购钓鱼竿#k(5元宝)#n#l ";
text +="\r\n#b#L19##r"+mmm+"换购火柴棒#k(5元宝)#n#l ";
text +="\r\n#b#L20##r"+mmm+"换购火炬同#k(5元宝)#n#l ";
cm.sendSimple("欢迎来到#r"+zz+"#k,这里是小七冒险岛岛屿( ^_^ )\r\n\r\n#r提示：#k\r\n1.你必须拥有足够的元宝才可以进行换购\r\n2.换购后不可退货"+
text);
}

} else if (status == 1) {

if (selection == 1) {
if(cm.getzb() >= yb){
if(cm.haveItem(1402014,1,true,false)){
cm.sendOk("你已经有一个温度计了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1402014); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1402014)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}

if (selection == 2) {
if(cm.getzb() >= yb){
if(cm.haveItem(1302063,1,true,false)){
cm.sendOk("你已经有一个火焰刀了了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();
	                
var type = ii.getInventoryType(1302063); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1302063)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);	
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}

if (selection == 3) {
if(cm.getzb() >= yb){
if(cm.haveItem(1302061,1,true,false)){
cm.sendOk("你已经有一个蔓藤鞭了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1302061); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1302061)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}

if (selection == 4) {
if(cm.getzb() >= yb){
if(cm.haveItem(1372031,1,true,false)){
cm.sendOk("你已经有一个圣贤仗了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1372031); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1372031)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}


if (selection == 5) {
if(cm.getzb() >= yb){
if(cm.haveItem(1322051,1,true,false)){
cm.sendOk("你已经有一个七夕了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1322051); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1322051)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}




if (selection == 6) {
if(cm.getzb() >= yb){
if(cm.haveItem(1302049,1,true,false)){
cm.sendOk("你已经有一个光线鞭子了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1302049); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1302049)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}



if (selection == 7) {
if(cm.getzb() >= yb){
if(cm.haveItem(1112907,1,true,false)){
cm.sendOk("你已经有一个小鱼戒指了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1112907); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1112907)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}



if (selection == 8) {
if(cm.getzb() >= yb){
if(cm.haveItem(1332032,1,true,false)){
cm.sendOk("你已经有一个圣诞树了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1332032); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1332032)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}




if (selection == 9) {
if(cm.getzb() >= yb){
if(cm.haveItem(1402013,1,true,false)){
cm.sendOk("你已经有一个白日剑了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1402013); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1402013)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}



if (selection == 10) {
if(cm.getzb() >= yb){
if(cm.haveItem(1372017,1,true,false)){
cm.sendOk("你已经有一个领路灯了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1372017); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1372017)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}

if (selection == 11) {
if(cm.getzb() >= yb){
if(cm.haveItem(1402044,1,true,false)){
cm.sendOk("你已经有一个南瓜灯了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1402044); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1402044)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}




if (selection == 12) {
if(cm.getzb() >= yb){
if(cm.haveItem(1432037,1,true,false)){
cm.sendOk("你已经有一个大将旗了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1432037); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1432037)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}





if (selection == 13) {
if(cm.getzb() >= yb){
if(cm.haveItem(1332021,1,true,false)){
cm.sendOk("你已经有一个乌龙茶了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1332021); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1332021)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}



if (selection == 14) {
if(cm.getzb() >= yb){
if(cm.haveItem(1092022,1,true,false)){
cm.sendOk("你已经有一个调色板了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1092022); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1092022)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}




if (selection == 15) {
if(cm.getzb() >= yb){
if(cm.haveItem(1302080,1,true,false)){
cm.sendOk("你已经有一个霓虹灯了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1302080); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1302080)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}



if (selection == 16) {
if(cm.getzb() >= yb){
if(cm.haveItem(1402063,1,true,false)){
cm.sendOk("你已经有一个樱花伞了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1402063); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1402063)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}



if (selection == 17) {
if(cm.getzb() >= yb){
if(cm.haveItem(1702116,1,true,false)){
cm.sendOk("你已经有一个中国结了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1702116); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1702116)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}



if (selection == 18) {
if(cm.getzb() >= yb){
if(cm.haveItem(1432039,1,true,false)){
cm.sendOk("你已经有一个钓鱼竿了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1432039); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1432039)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}



if (selection == 19) {
if(cm.getzb() >= yb){
if(cm.haveItem(1302128,1,true,false)){
cm.sendOk("你已经有一个火柴棒了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1302128); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1302128)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}



if (selection == 20) {
if(cm.getzb() >= yb){
if(cm.haveItem(1302087,1,true,false)){
cm.sendOk("你已经有一个火炬同了！");
cm.dispose();
}else{
cm.setzb(-yb);
var ii = net.sf.odinms.server.MapleItemInformationProvider.getInstance();		                
var type = ii.getInventoryType(1302087); //获得装备的类形
var toDrop = ii.randomizeStats(ii.getEquipById(1302087)).copy(); // 生成一个Equip类
//
toDrop.setLocked(1);
cm.getPlayer().getInventory(type).addItem(toDrop);//将这个装备放入包中
cm.getC().getSession().write(net.sf.odinms.tools.MaplePacketCreator.addInventorySlot(type, toDrop)); //刷新背包		
cm.getChar().saveToDB(true);
cm.sendOk("你已经成功换购道具！");
cm.dispose();
}					
}else{
cm.sendOk("你没有足够的元宝！");
cm.dispose();
}
}

//--------------------------


}
}
}
