package ru.omegapps.wisherapp.data_agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ru.omegapps.wisherapp.dto.Wish;
import ru.omegapps.wisherapp.dto.WishBlock;
import ru.omegapps.wisherapp.enums.WishBlockEnum;
import ru.omegapps.wisherapp.enums.WishEnum;
import ru.omegapps.wisherapp.interfaces.DataAgent;

public class LocalDataAgent implements DataAgent {

    public static ArrayList<Wish> wishes;
    public static ArrayList<WishBlock> blocks;

    static {
        wishes = new ArrayList<>();
        wishes.add(new Wish(WishEnum.WishOne));
        wishes.add(new Wish(WishEnum.WishTwo));
        wishes.add(new Wish(WishEnum.WishThree));

        blocks = new ArrayList<>();
        blocks.add(new WishBlock(WishBlockEnum.WishBlockOne));
        blocks.add(new WishBlock(WishBlockEnum.WishBlockThree));
        blocks.add(new WishBlock(WishBlockEnum.WishBlockTwo));
        blocks.add(new WishBlock(WishBlockEnum.WishBlockFour));
        blocks.add(new WishBlock(WishBlockEnum.WishBlockFive));
    }

    @Override
    public ArrayList<WishBlock> getBlocksByTags(List<String> tags, List<String> filters) {
        return (ArrayList<WishBlock>) blocks.stream()
                .filter(block -> block.getFilters().containsAll(filters))
                .filter(block -> !(Collections.disjoint(block.getTags(), tags) && !tags.isEmpty()))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean pushWish(Wish wish, String user) {
        wishes.add(wish);
        return true;
    }

    @Override
    public Boolean pushBlock(WishBlock wishBlock, String user) {
        blocks.add(wishBlock);
        return true;
    }

    @Override
    public List<Wish> getWishes() {
        return wishes;
    }

    @Override
    public List<WishBlock> getBlocks() {
        return blocks;
    }
}
