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

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.holders.DamageTakenHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.OutgoingPackets;

/**
 * @author Mobius
 */
public class ExDieInfo implements IClientOutgoingPacket
{
	private final Collection<Item> _droppedItems;
	private final Collection<DamageTakenHolder> _lastDamageTaken;
	
	public ExDieInfo(Collection<Item> droppedItems, Collection<DamageTakenHolder> lastDamageTaken)
	{
		_droppedItems = droppedItems;
		_lastDamageTaken = lastDamageTaken;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_DIE_INFO.writeId(packet);
		packet.writeH(_droppedItems.size());
		for (Item item : _droppedItems)
		{
			packet.writeD(item.getId());
			packet.writeD(item.getEnchantLevel());
			packet.writeD((int) item.getCount());
		}
		packet.writeH(_lastDamageTaken.size());
		for (DamageTakenHolder damageHolder : _lastDamageTaken)
		{
			if (damageHolder.getCreature().isNpc())
			{
				packet.writeH(1);
				packet.writeD(damageHolder.getCreature().getId());
				packet.writeS("");
			}
			else
			{
				final Clan clan = damageHolder.getCreature().getClan();
				packet.writeH(0);
				packet.writeS(damageHolder.getCreature().getName());
				packet.writeS(clan == null ? "" : clan.getName());
			}
			packet.writeD(damageHolder.getSkillId());
			packet.writeF(damageHolder.getDamage());
			packet.writeH(0); // damage type
		}
		return true;
	}
}
