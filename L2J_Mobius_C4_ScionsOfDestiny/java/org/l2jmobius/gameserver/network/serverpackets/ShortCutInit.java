/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.Collection;

import org.l2jmobius.gameserver.model.ShortCut;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * ShortCutInit format d *(1dddd)/(2ddddd)/(3dddd)
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/03/27 15:29:39 $
 */
public class ShortCutInit extends ServerPacket
{
	private Collection<ShortCut> _shortCuts;
	private Player _player;
	
	public ShortCutInit(Player player)
	{
		_player = player;
		if (_player == null)
		{
			return;
		}
		_shortCuts = _player.getAllShortCuts();
	}
	
	@Override
	public void write()
	{
		ServerPackets.SHORT_CUT_INIT.writeId(this);
		writeInt(_shortCuts.size());
		for (ShortCut sc : _shortCuts)
		{
			writeInt(sc.getType());
			writeInt(sc.getSlot() + (sc.getPage() * 12));
			writeInt(sc.getId());
			if (sc.getLevel() > -1)
			{
				writeInt(_player.getSkillLevel(sc.getId()));
			}
			writeInt(1);
		}
	}
}
