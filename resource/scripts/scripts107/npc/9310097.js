/* ===========================================================
			注释(cm.sendSimple\cm.itemQuantity())
	脚本类型: 		NPC
	所在地图:		自由市场
	脚本名字:		点播音乐
==============================================================
制作时间：2010年11月21日 22:34:25
制作人员：笔芯
=============================================================
for(var i = 1;i<=5;i++){
				if(cm.getPlayer().getInventory(net.sf.odinms.client.MapleInventoryType.getByType(i)).isFull()){
					cm.startPopMessage(cm.getPlayer().getId(), "您必须让自己的背包腾出一格。");
					cm.dispose();
					return;
				}
			}
*/

var a = 0;

var music = Array(
Array("Bgm00/SleepyWood",60,80),
Array("Bgm00/FloralLife",60,80),
Array("Bgm00/GoPicnic",60,80),
Array("Bgm00/Nightmare",60,80),
Array("Bgm00/RestNPeace",60,80),
Array("Bgm01/AncientMove",60,80),
Array("Bgm01/MoonlightShadow",60,80),
Array("Bgm01/WhereTheBarlogFrom",60,80),
Array("Bgm01/CavaBien",60,80),
Array("Bgm01/HighlandStar",60,80),
Array("Bgm01/BadGuys",60,80),
Array("Bgm02/MissingYou",60,80),
Array("Bgm02/WhenTheMorningComes",60,80),
Array("Bgm02/EvilEyes",60,80),
Array("Bgm02/JungleBook",60,80),
Array("Bgm02/AboveTheTreetops",60,80),
Array("Bgm03/Subway",60,80),
Array("Bgm03/Elfwood",60,80),
Array("Bgm03/BlueSky",60,80),
Array("Bgm03/Beachway",60,80),
Array("Bgm03/SnowyVillage",60,80),
Array("Bgm04/PlayWithMe",60,80),
Array("Bgm04/WhiteChristmas",60,80),
Array("Bgm04/UponTheSky",60,80),
Array("Bgm04/ArabPirate",60,80),
Array("Bgm04/Shinin'Harbor",60,80),
Array("Bgm04/WarmRegard",60,80),
Array("Bgm05/WolfWood",60,80),
Array("Bgm05/DownToTheCave",60,80),
Array("Bgm05/AbandonedMine",60,80),
Array("Bgm05/MineQuest",60,80),
Array("Bgm05/HellGate",60,80),
Array("Bgm06/FinalFight",60,80),
Array("Bgm06/WelcomeToTheHell",60,80),
Array("Bgm06/ComeWithMe",60,80),
Array("Bgm06/FlyingInABlueDream",60,80),
Array("Bgm06/FantasticThinking",60,80),
Array("Bgm07/WaltzForWork",60,80),
Array("Bgm07/WhereverYouAre",60,80),
Array("Bgm07/FunnyTimeMaker",60,80),
Array("Bgm07/HighEnough",60,80),
Array("Bgm07/Fantasia",60,80),
Array("Bgm08/LetsMarch",60,80),
Array("Bgm08/ForTheGlory",60,80),
Array("Bgm08/FindingForest",60,80),
Array("Bgm08/LetsHuntAliens",60,80),
Array("Bgm08/PlotOfPixie",60,80),
Array("Bgm09/DarkShadowl",60,80),
Array("Bgm09/TheyMenacingYou",60,80),
Array("Bgm09/FairyTale",60,80),
Array("Bgm09/FairyTalediffvers",60,80),
Array("Bgm09/TimeAttack",60,80),
Array("Bgm10/Timeless",60,80),
Array("Bgm10/TimelessB",60,80),
Array("Bgm10/BizarreTales",60,80),
Array("Bgm10/TheWayGrotesque",60,80),
Array("Bgm10/Eregos",60,80),
Array("Bgm11/BlueWorld",60,80),
Array("Bgm11/Aquarium",60,80),
Array("Bgm11/ShiningSea",60,80),
Array("Bgm11/DownTown",60,80),
Array("Bgm11/DarkMountain",60,80),
Array("Bgm12/AquaCave",60,80),
Array("Bgm12/DeepSee",60,80),
Array("Bgm12/WaterWay",60,80),
Array("Bgm12/AcientRemain",60,80),
Array("Bgm12/RuinCastle",60,80),
Array("Bgm12/Dispute",60,80),
Array("Bgm13/CokeTown",60,80),
Array("Bgm13/Leafre",60,80),
Array("Bgm13/Minar'sDream",60,80),
Array("Bgm13/AcientForest",60,80),
Array("Bgm13/TowerOfGoddess",60,80),
Array("Bgm14/DragonLoad",60,80),
Array("Bgm14/HonTale",60,80),
Array("Bgm14/CaveOfHontale",60,80),
Array("Bgm14/DragonNest",60,80),
Array("Bgm14/Ariant",60,80),
Array("Bgm14/HotDesert",60,80),
Array("Bgm15/MureungHill",60,80),
Array("Bgm15/MureungForest",60,80),
Array("Bgm15/WhiteHerb",60,80),
Array("Bgm15/Pirate",60,80),
Array("Bgm15/SunsetDesert",60,80),
Array("BgmEvent/FunnyRabbit",60,80),
Array("BgmEvent/FunnyRabbitFaster",60,80),
Array("BgmGL/amoria",60,80),
Array("BgmGL/chapel",60,80),
Array("BgmGL/cathedral",60,80),
Array("BgmGL/Amorianchallenge",60,80),
Array("BgmJp/Feeling",60,80),
Array("BgmJp/BizarreForest",60,80),
Array("BgmJp/Hana",60,80),
Array("BgmJp/Yume",60,80),
Array("BgmJp/Bathroom",60,80),
Array("BgmJp/BattleField",60,80),
Array("BgmJp/FirstStepMaster",60,80),
Array("BgmJp/CastleOutSide",60,80),
Array("BgmJp/CastleInside",60,80),
Array("BgmJp/CastleBoss",60,80),
Array("BgmJp/CastleTrap",60,80),
Array("BgmMY/KualaLumpur",60,80),
Array("BgmMY/Highland",60,80),
Array("BgmJp2/Kamuna",60,80),
Array("BgmJp2/Odaiba",60,80),
Array("BgmJp2/Park",60,80),
Array("BgmJp2/Akiabara",60,80),
Array("BgmJp2/Office",60,80),
Array("BgmJp2/Tokyosky",60,80),
Array("BgmJp2/Rockbongi1",60,80),
Array("BgmJp2/Rockbongi2",60,80)
);//音乐
var SeleMusic = -1;

function start() {
	a = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	 if (a >= 0 && mode != 1){
		 cm.dispose();
	}else{
    if (mode == -1) {
		cm.dispose();
    } else {
        if (mode == 1)
            a++;
        else
            a--;
			
			
			
			
        if (a == -1){
        }else if (a == 0){
					var selStr = "请选择需要点播的（使用点卷）\r\n#r -目前点卷为："+cm.getChar().getNX()+"#b";
					for (var i = 0; i < music.length; i++) {
						selStr += "\r\n#L" + i + "# "+music[i][0]+" （"+music[i][1]+"）点卷 #l";
					}
					cm.sendSimple(selStr);
				}else if (a == 1){
					SeleMusic = selection;
					cm.sendYesNo("确定要点播吗？这需要"+music[SeleMusic][1]+"点卷。");
				}else if (a == 2){
					if (cm.getChar().getNX() >= music[SeleMusic][1]){
					cm.changeMusic(music[SeleMusic][0]);
					cm.sendOk("点播成功了。")
					cm.gainNX(-music[SeleMusic][1])
                                        cm.serverNotice(5,"[音乐点播]热心的玩家:"+cm.getChar().getName()+" 在市场为我们的岛民点了一首音乐，请欣赏！！");
					}else{
					cm.sendOk("对不起，你没有足够的点卷。")	
					}
					cm.dispose();
				}
}
}
}