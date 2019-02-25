/* ===========================================================
			注释(qm.sendSimple\qm.itemQuantity(5420008))
	脚本类型: 		QUEST
	脚本名字:		
	完成任务函数：qm.forceCompleteQuest();
	接受任务函数：qm.forceStartQuest();
=============================================================
制作时间：
制作人员：笔芯
=============================================================
*/
importPackage(net.sf.odinms.client);


var a = -1;

function end(mode, type, selection) {//接任务
	if (mode == -1) {
		qm.dispose();
	} else {
		if (mode == 1)
			a++;
		else
			a--;
			if(a == -1){
				qm.sendOk("请慎重选择。慎重一点不是什么坏事。")
				qm.dispose();
			}else if (a == 0) {
				qm.sendYesNo("谢谢你这么爽快地接受……你真的经过深思熟虑了吗？唤灵斗师虽然很强，但也很难操作。在近战攻击的同时，还必须闪躲远方的怪物，因此在操控方面要求很高。你能做到吗？希望你仔细考虑清楚之后再回答我。")
			} else if (a == 1){
				qm.changeJob(MapleJob.getById(3200));
				/*var statup = new java.util.ArrayList();
				var p = qm.getPlayer();
				var totAp = p.getRemainingAp() + p.getStr() + p.getDex() + p.getInt() + p.getLuk();		
				p.setStr(4);
				p.setDex(4);
				p.setInt(4);
				p.setLuk(4);
				p.setRemainingAp (totAp - 16);
				statup.add(new net.sf.odinms.tools.Pair(MapleStat.STR, java.lang.Integer.valueOf(4)));
				statup.add(new net.sf.odinms.tools.Pair(MapleStat.DEX, java.lang.Integer.valueOf(4)));
				statup.add(new net.sf.odinms.tools.Pair(MapleStat.LUK, java.lang.Integer.valueOf(4)));
				statup.add(new net.sf.odinms.tools.Pair(MapleStat.INT, java.lang.Integer.valueOf(4)));
				statup.add(new net.sf.odinms.tools.Pair(MapleStat.AVAILABLEAP, java.lang.Integer.valueOf(p.getRemainingAp())));
				qm.getC().getSession().write (net.sf.odinms.tools.MaplePacketCreator.updatePlayerStats(statup));
				*/
                qm.gainItem(1142242,1);//特别课程新生
              
			   qm.forceCompleteQuest();
				qm.sendOk("哈哈哈！很好！欢迎你正式成为反抗者。从现在开始，你就是唤灵斗师了。希望你能拿着神圣的长杖，抵抗黑魔法师！")
			}else if (a == 2){
				qm.sendOk("如果在外面提到唤灵斗师的话，难免会引起黑色之翼的怀疑。所以从现在开始，你要叫我班主任。你是来教室接受特别课程的学生。呵呵呵……有意思吧？我的特别课程会把你培养成最好的唤灵斗师。")
			} else if (a == 3){
				// qm.startPopMessage(cm.getPlayer().getId(), "获得了<特殊课程新生>称号。");
				 qm.dispose();
		}//status
	}//mode
}//function
