package com.spectplus.ak.utils.itemstack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by AnyKwey
 */
public class ItemBuilder {
    private final Map<UUID, String> textureCache;
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(final ItemStack item) {
        this.textureCache = new HashMap<>();
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(final Material material) {
        this(material, 1, 0);
    }

    public ItemBuilder(final Material material, final int amount) {
        this(material, amount, 0);
    }

    public ItemBuilder(final Material material, final int amount, final int data) {
        this.textureCache = new HashMap<>();
        this.item = new ItemStack(material, amount, (short) data);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder setDurability(final int durability) {
        item.setDurability((short) durability);
        return this;
    }

    public ItemBuilder addEnchantment(final Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addEnchantment(final Map<Enchantment, Integer> enchantments) {
        enchantments.forEach(this::addEnchantment);
        return this;
    }

    public ItemBuilder removeEnchantment(final Enchantment enchantment) {
        meta.removeEnchant(enchantment);
        return this;
    }

    public ItemBuilder addItemFlags(final ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder removeItemFlags(final ItemFlag... flags) {
        meta.removeItemFlags(flags);
        return this;
    }

    public ItemBuilder setSkullOwner(final String texture) {
        if (item.getType() == Material.PLAYER_HEAD) {
            final SkullMeta skullMeta = (SkullMeta) meta;
            final GameProfile profile = new GameProfile(UUID.randomUUID(), null);

            profile.getProperties().put("textures", new Property("textures", texture));

            try {
                final Field field = skullMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(skullMeta, profile);
                field.setAccessible(false);
                item.setItemMeta(skullMeta);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    public ItemBuilder setSkullOwner(final UUID uuid) {
        if (textureCache.containsKey(uuid)) {
            setSkullOwner(textureCache.get(uuid));
            return this;
        }

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "")).openConnection();
            connection.setReadTimeout(5000);

            final JsonObject result = new JsonParser().parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
            final JsonArray properties = result.get("properties").getAsJsonArray();
            final String texture = properties.get(0).getAsJsonObject().get("value").getAsString();

            textureCache.put(uuid, texture);
            return setSkullOwner(texture);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public ItemBuilder setPattern(final int position, final Pattern pattern) {
        if (item.getType().name().contains("BANNER") && !item.getType().name().contains("PATTERN")) {
            final BannerMeta bannerMeta = (BannerMeta) meta;

            bannerMeta.setPattern(position, pattern);
            item.setItemMeta(bannerMeta);
        }

        return this;
    }

    public ItemBuilder addPattern(final Pattern pattern) {
        if (item.getType().name().contains("BANNER") && !item.getType().name().contains("PATTERN")) {
            final BannerMeta bannerMeta = (BannerMeta) meta;

            bannerMeta.addPattern(pattern);
            item.setItemMeta(bannerMeta);
        }

        return this;
    }

    public ItemBuilder removePattern(final int position) {
        if (item.getType().name().contains("BANNER") && !item.getType().name().contains("PATTERN")) {
            final BannerMeta bannerMeta = (BannerMeta) meta;

            bannerMeta.removePattern(position);
            item.setItemMeta(bannerMeta);
        }

        return this;
    }

    public ItemBuilder setBookPage(final int page, final String content) {
        if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITTEN_BOOK) {
            final BookMeta bookMeta = (BookMeta) meta;

            bookMeta.setPage(page, content);
            item.setItemMeta(bookMeta);
        }
        return this;
    }

    public ItemBuilder addBookPage(final String... pages) {
        if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITTEN_BOOK) {
            final BookMeta bookMeta = (BookMeta) meta;

            bookMeta.addPage(pages);
            item.setItemMeta(bookMeta);
        }

        return this;
    }

    public ItemBuilder addPotionEffect(final PotionEffectType type, final int strength, final int duration, final boolean overwrite) {
        if (item.getType() == Material.POTION) {
            final PotionMeta potionMeta = (PotionMeta) meta;

            potionMeta.addCustomEffect(new PotionEffect(type, strength, duration), overwrite);
            item.setItemMeta(potionMeta);
        }
        return this;
    }

    public ItemBuilder removePotionEffect(final PotionEffectType type) {
        if (item.getType() == Material.POTION) {
            final PotionMeta potionMeta = (PotionMeta) meta;

            potionMeta.removeCustomEffect(type);
            item.setItemMeta(potionMeta);
        }

        return this;
    }

    public Material getType() {
        return item.getType();
    }

    public ItemBuilder setType(final Material material) {
        item.setType(material);
        return this;
    }

    public String getDisplayName() {
        return meta.hasDisplayName() ? meta.getDisplayName() : "";
    }

    public ItemBuilder setDisplayName(final String displayName) {
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        return this;
    }

    public int getAmount() {
        return item.getAmount();
    }

    public ItemBuilder setAmount(final int amount) {
        item.setAmount(amount);
        return this;
    }

    public List<String> getLore() {
        return meta.hasLore() ? meta.getLore() : new ArrayList<>();
    }

    public ItemBuilder setLore(final List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder setLore(final String... lore) {
        setLore(Arrays.asList(lore));
        return this;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return meta.hasEnchants() ? meta.getEnchants() : new HashMap<>();
    }

    public Set<ItemFlag> getItemFlags() {
        return meta.getItemFlags().isEmpty() ? new HashSet<>() : meta.getItemFlags();
    }

    public DyeColor getBaseColor() {
        if (item.getType().name().contains("BANNER") && !item.getType().name().contains("PATTERN")) {
            return ((BannerMeta) meta).getBaseColor();
        }

        return null;
    }

    public ItemBuilder setBaseColor(final DyeColor color) {
        if (item.getType().name().contains("BANNER") && !item.getType().name().contains("PATTERN")) {
            final BannerMeta bannerMeta = (BannerMeta) meta;

            bannerMeta.setBaseColor(color);
            item.setItemMeta(bannerMeta);
        }

        return this;
    }

    public List<Pattern> getPatterns() {
        if (item.getType().name().contains("BANNER") && !item.getType().name().contains("PATTERN")) {
            return ((BannerMeta) meta).getPatterns();
        }

        return null;
    }

    public ItemBuilder setPatterns(final List<Pattern> patterns) {
        if (item.getType().name().contains("BANNER") && !item.getType().name().contains("PATTERN")) {
            final BannerMeta bannerMeta = (BannerMeta) meta;

            bannerMeta.setPatterns(patterns);
            item.setItemMeta(bannerMeta);
        }

        return this;
    }

    public ItemBuilder setPatterns(final Pattern... patterns) {
        if (item.getType().name().contains("BANNER") && !item.getType().name().contains("PATTERN")) {
            final BannerMeta bannerMeta = (BannerMeta) meta;

            bannerMeta.setPatterns(Arrays.asList(patterns));
            item.setItemMeta(bannerMeta);
        }

        return this;
    }

    public Pattern getPattern(int position) {
        if (item.getType().name().contains("BANNER") && !item.getType().name().contains("PATTERN")) {
            return ((BannerMeta) meta).getPattern(position);
        }

        return null;
    }

    public int getPatternCount() {
        if (item.getType().name().contains("BANNER") && !item.getType().name().contains("PATTERN")) {
            return ((BannerMeta) meta).numberOfPatterns();
        }

        return 0;
    }

    public Color getLeatherArmorColor() {
        return item.getType() == Material.LEATHER_BOOTS || item.getType() == Material.LEATHER_LEGGINGS || item.getType() == Material.LEATHER_CHESTPLATE || item.getType() == Material.LEATHER_HELMET ? ((LeatherArmorMeta) meta).getColor() : null;
    }

    public ItemBuilder setLeatherArmorColor(final Color color) {
        if (item.getType() == Material.LEATHER_BOOTS || item.getType() == Material.LEATHER_LEGGINGS || item.getType() == Material.LEATHER_CHESTPLATE || item.getType() == Material.LEATHER_HELMET) {
            final LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;

            leatherArmorMeta.setColor(color);
            item.setItemMeta(leatherArmorMeta);
        }

        return this;
    }

    public String getBookTitle() {
        return item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITTEN_BOOK ? ((BookMeta) meta).getTitle() : "";
    }

    public ItemBuilder setBookTitle(final String title) {
        if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITTEN_BOOK) {
            final BookMeta bookMeta = (BookMeta) meta;

            bookMeta.setTitle(title);
            item.setItemMeta(bookMeta);
        }
        return this;
    }

    public String getBookAuthor() {
        return item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITTEN_BOOK ? ((BookMeta) meta).getAuthor() : "";
    }

    public ItemBuilder setBookAuthor(final String author) {
        if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITTEN_BOOK) {

            final BookMeta bookMeta = (BookMeta) meta;

            bookMeta.setAuthor(author);
            item.setItemMeta(bookMeta);
        }
        return this;
    }

    public List<String> getBookPages() {
        return item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITTEN_BOOK ? ((BookMeta) meta).getPages() : new ArrayList<>();
    }

    public ItemBuilder setBookPages(final List<String> pages) {
        if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITTEN_BOOK) {
            final BookMeta bookMeta = (BookMeta) meta;

            bookMeta.setPages(pages);
            item.setItemMeta(bookMeta);
        }
        return this;
    }

    public ItemBuilder setBookPages(final String... pages) {
        if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITTEN_BOOK) {
            setBookPages(Arrays.asList(pages));
        }

        return this;
    }

    public int getBookPageCount() {
        return item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.WRITTEN_BOOK ? ((BookMeta) meta).getPageCount() : 0;
    }

    public List<PotionEffect> getPotionEffects() {
        return item.getType() == Material.POTION ? ((PotionMeta) meta).getCustomEffects() : new ArrayList<>();
    }

    public boolean hasPotionEffect(final PotionEffectType type) {
        return item.getType() == Material.POTION && ((PotionMeta) meta).hasCustomEffect(type);
    }

    public boolean isMapScaling() {
        return item.getType() == Material.MAP && ((MapMeta) meta).isScaling();
    }

    public ItemBuilder setMapScaling(final boolean scaling) {
        if (item.getType() == Material.MAP) {
            final MapMeta mapMeta = (MapMeta) meta;

            mapMeta.setScaling(scaling);
            item.setItemMeta(mapMeta);
        }

        return this;
    }

    public ItemBuilder repair() {
        if (item.getType().getMaxDurability() > 0)
            item.setDurability((short) 0);
        return this;
    }

    public ItemStack toItemStack() {
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getHead(Player player){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwner(player.getName());
        item.setItemMeta(skull);
        return item;
    }

    public static ItemStack createSkull(String url, String name) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        }
        catch (IllegalArgumentException|NoSuchFieldException|SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
}