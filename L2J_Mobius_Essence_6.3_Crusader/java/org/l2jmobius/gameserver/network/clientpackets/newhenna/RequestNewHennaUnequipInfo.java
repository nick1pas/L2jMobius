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
package org.l2jmobius.gameserver.network.clientpackets.newhenna;

import org.l2jmobius.commons.network.ReadablePacket;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.henna.Henna;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.ClientPacket;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.newhenna.NewHennaUnequip;

/**
 * @author Index
 */
public class RequestNewHennaUnequipInfo implements ClientPacket
{
	private int _hennaId;
	
	@Override
	public void read(ReadablePacket packet)
	{
		_hennaId = packet.readInt();
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		Henna henna = null;
		int slotId = 0;
		for (int slot = 1; slot <= 4; slot++)
		{
			if (player.getHenna(slot) == null)
			{
				continue;
			}
			if (player.getHenna(slot).getDyeId() == _hennaId)
			{
				henna = player.getHenna(slot);
				slotId = slot;
				break;
			}
		}
		if ((henna == null) || !client.getFloodProtectors().canPerformTransaction())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(new NewHennaUnequip(slotId, 0));
			return;
		}
		
		if (player.getAdena() >= henna.getCancelFee())
		{
			player.removeHenna(slotId);
			player.sendPacket(new NewHennaUnequip(slotId, 1));
		}
		else
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ADENA);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(new NewHennaUnequip(slotId, 0));
		}
	}
}
