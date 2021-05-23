package ru.omegapps.wisherapp.interfaces;

import java.util.ArrayList;
import java.util.List;

import ru.omegapps.wisherapp.dto.Wish;
import ru.omegapps.wisherapp.dto.WishBlock;

public interface DataAgent {
    ArrayList<WishBlock> getBlocksByTags(List<String> tags, List<String> filters);
    Boolean pushWish(Wish wish, String user);
    Boolean pushBlock(WishBlock wishBlock, String user);
    List<Wish> getWishes();
    List<WishBlock> getBlocks();
}
