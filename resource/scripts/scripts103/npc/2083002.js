/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/* Pison
	Warp NPC to Lith Harbor (104000000)
	located in Florina Beach (110000000)
*/

importPackage(net.sf.odinms.server.maps);

var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status >= 2 && mode == 0) {
			cm.sendOk("那好吧,下次再见.");
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendNext("你真的想离开这里？若选择离开，你将重新踏上与暗黑龙王进行战斗的旅程！");
		} else if (status == 1) {
			cm.sendNextPrev("我将传送你到 #b神木村#k，一路顺风.")
		} else if (status == 2) {
			if (cm.getMeso() < 0) {
				cm.sendOk("你没有忘记什么东西吧.")
				cm.dispose();
			} else {
				cm.sendYesNo("你确定启程 #b神木村#k? 好吧，那我们就启程吧，你没有忘记你的东西吧？");
			}
		} else if (status == 3) {
			var map = cm.getChar().getSavedLocation(SavedLocationType.FLORINA);
			if (map == -1) {
				map = 240000000;
			}
			cm.warp(map, 0);
			cm.dispose();
		}
	}
}