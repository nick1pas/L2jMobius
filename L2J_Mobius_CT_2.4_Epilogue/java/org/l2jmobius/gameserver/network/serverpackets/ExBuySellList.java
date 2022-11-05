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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.buylist.BuyListHolder;
import org.l2jmobius.gameserver.model.buylist.Product;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author ShanSoft
 */
public class ExBuySellList extends ServerPacket
{
	private final int _buyListId;
	private final List<Product> _buyList = new ArrayList<>();
	private final long _money;
	private double _taxRate = 0;
	private Collection<Item> _sellList = null;
	private Collection<Item> _refundList = null;
	private final boolean _done;
	
	public ExBuySellList(Player player, BuyListHolder list, boolean done)
	{
		_money = player.getAdena();
		_buyListId = list.getListId();
		for (Product item : list.getProducts())
		{
			if (item.hasLimitedStock() && (item.getCount() <= 0))
			{
				continue;
			}
			_buyList.add(item);
		}
		_sellList = player.getInventory().getAvailableItems(false, false, false);
		if (player.hasRefund())
		{
			_refundList = player.getRefund().getItems();
		}
		_done = done;
	}
	
	public ExBuySellList(Player player, BuyListHolder list, double taxRate, boolean done)
	{
		_money = player.getAdena();
		_taxRate = taxRate;
		_buyListId = list.getListId();
		for (Product item : list.getProducts())
		{
			if (item.hasLimitedStock() && (item.getCount() <= 0))
			{
				continue;
			}
			_buyList.add(item);
		}
		_sellList = player.getInventory().getAvailableItems(false, false, false);
		if (player.hasRefund())
		{
			_refundList = player.getRefund().getItems();
		}
		_done = done;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_BUY_SELL_LIST.writeId(this);
		writeLong(_money);
		writeInt(_buyListId);
		writeShort(_buyList.size());
		for (Product item : _buyList)
		{
			writeShort(item.getItem().getType1());
			writeInt(0); // objectId
			writeInt(item.getItemId());
			writeLong(item.getCount() < 0 ? 0 : item.getCount());
			writeShort(item.getItem().getType2());
			writeShort(0); // ?
			if (item.getItem().getType1() != ItemTemplate.TYPE1_ITEM_QUESTITEM_ADENA)
			{
				writeInt(item.getItem().getBodyPart());
				writeShort(0); // item enchant level
				writeShort(0); // ?
				writeShort(0);
			}
			else
			{
				writeInt(0);
				writeShort(0);
				writeShort(0);
				writeShort(0);
			}
			if ((item.getItemId() >= 3960) && (item.getItemId() <= 4026))
			{
				writeLong((long) (item.getPrice() * Config.RATE_SIEGE_GUARDS_PRICE));
			}
			else
			{
				writeLong((long) (item.getPrice() * (1 + (_taxRate / 2))));
			}
			// T1
			for (byte i = 0; i < 8; i++)
			{
				writeShort(0);
			}
			writeShort(0); // Enchant effect 1
			writeShort(0); // Enchant effect 2
			writeShort(0); // Enchant effect 3
		}
		if ((_sellList != null) && !_sellList.isEmpty())
		{
			writeShort(_sellList.size());
			for (Item item : _sellList)
			{
				writeShort(item.getTemplate().getType1());
				writeInt(item.getObjectId());
				writeInt(item.getId());
				writeLong(item.getCount());
				writeShort(item.getTemplate().getType2());
				writeShort(0);
				writeInt(item.getTemplate().getBodyPart());
				writeShort(item.getEnchantLevel());
				writeShort(0);
				writeShort(0);
				writeLong(Config.MERCHANT_ZERO_SELL_PRICE ? 0 : item.getTemplate().getReferencePrice() / 2);
				// T1
				writeShort(item.getAttackElementType());
				writeShort(item.getAttackElementPower());
				for (byte i = 0; i < 6; i++)
				{
					writeShort(item.getElementDefAttr(i));
				}
				writeShort(0); // Enchant effect 1
				writeShort(0); // Enchant effect 2
				writeShort(0); // Enchant effect 3
			}
		}
		else
		{
			writeShort(0);
		}
		if ((_refundList != null) && !_refundList.isEmpty())
		{
			writeShort(_refundList.size());
			int idx = 0;
			for (Item item : _refundList)
			{
				writeInt(idx++);
				writeInt(item.getId());
				writeLong(item.getCount());
				writeShort(item.getTemplate().getType2());
				writeShort(0); // ?
				writeShort(item.getEnchantLevel());
				writeShort(0); // ?
				writeLong(Config.MERCHANT_ZERO_SELL_PRICE ? 0 : (item.getTemplate().getReferencePrice() / 2) * item.getCount());
				// T1
				writeShort(item.getAttackElementType());
				writeShort(item.getAttackElementPower());
				for (byte i = 0; i < 6; i++)
				{
					writeShort(item.getElementDefAttr(i));
				}
				writeShort(0); // Enchant effect 1
				writeShort(0); // Enchant effect 2
				writeShort(0); // Enchant effect 3
			}
		}
		else
		{
			writeShort(0);
		}
		writeByte(_done);
	}
}
