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
package org.l2jmobius.gameserver.network.serverpackets.collection;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.l2jmobius.gameserver.data.xml.CollectionData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.PlayerCollectionData;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Berezkin Nikolay
 */
public class ExCollectionInfo extends ServerPacket
{
	final Player _Player;
	final List<PlayerCollectionData> _categoryList;
	final Set<Integer> _collections;
	final List<Integer> _favoriteList;
	final int _category;
	
	public ExCollectionInfo(Player player, int category)
	{
		_Player = player;
		_categoryList = player.getCollections().stream().filter(it -> CollectionData.getInstance().getCollection(it.getCollectionId()).getCategory() == category).collect(Collectors.toList());
		_collections = _categoryList.stream().map(PlayerCollectionData::getCollectionId).collect(Collectors.toSet());
		_favoriteList = player.getCollectionFavorites();
		_category = category;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_COLLECTION_INFO.writeId(this);
		writeInt(_collections.size()); // size
		for (Integer collection : _collections)
		{
			final List<PlayerCollectionData> collectionCurrent = _categoryList.stream().filter(it -> it.getCollectionId() == collection).collect(Collectors.toList());
			writeInt(collectionCurrent.size());
			for (PlayerCollectionData current : collectionCurrent)
			{
				writeByte(current.getIndex());
				writeInt(current.getItemId());
				writeByte(CollectionData.getInstance().getCollection(collection).getItems().get(current.getIndex()).getEnchantLevel()); // enchant level
				writeByte(0); // unk flag for item
				writeByte(0); // unk flag for item
				writeInt(1); // count
			}
			writeShort(collection);
		}
		writeInt(_favoriteList.size()); // favourite size
		for (int favoriteCollection : _favoriteList)
		{
			writeShort(favoriteCollection);
		}
		writeInt(0);
		// loop unk
		// 1 h
		// d
		// h
		// loop end
		writeByte(_category);
		writeShort(0);
	}
}
