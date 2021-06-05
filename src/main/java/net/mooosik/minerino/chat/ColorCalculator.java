package net.mooosik.minerino.chat;

import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class ColorCalculator {


    private static Formatting[] colors = Arrays.stream(Formatting.values()).filter(Formatting::isColor).toArray(Formatting[]::new);

    static HashMap<Integer, HashMap<Integer,HashMap<Integer, Formatting>>> RGB, GBR, BRG;


    private static Formatting[][][] colorMap;

    public static void setup() {

        colorMap = new Formatting[4][4][4];
        for (Formatting formatting: colors
        ) {
            Color color = new Color(formatting.getColorValue());

            colorMap[color.getRed()/85][color.getGreen()/85][color.getBlue()/85] = formatting;

        }

        RGB = new HashMap<>();
        GBR = new HashMap<>();
        BRG = new HashMap<>();

        for (Formatting formatting: colors
             ) {
            Color color = new Color(formatting.getColorValue());

            HashMap<Integer, HashMap<Integer, Formatting>> greens;
            HashMap<Integer, Formatting> blues;

            if(!RGB.containsKey(color.getRed())) {

                RGB.put(color.getRed(), new HashMap<>());
            }
            greens = RGB.get(color.getRed());

            if(!greens.containsKey(color.getGreen())) {

                greens.put(color.getGreen(), new HashMap<>());
            }
            blues = greens.get(color.getGreen());


            if(!blues.containsKey(color.getBlue())) {

                blues.put(color.getBlue(), formatting);
            }

        }

        for (Formatting formatting: colors
        ) {
            Color color = new Color(formatting.getColorValue());

            HashMap<Integer, HashMap<Integer, Formatting>> blues;
            HashMap<Integer, Formatting> reds;

            if(!GBR.containsKey(color.getGreen())) {

                GBR.put(color.getGreen(), new HashMap<>());
            }
            blues = GBR.get(color.getGreen());

            if(!blues.containsKey(color.getBlue())) {

                blues.put(color.getBlue(), new HashMap<>());
            }
            reds = blues.get(color.getBlue());


            if(!reds.containsKey(color.getRed())) {

                reds.put(color.getRed(), formatting);
            }

        }

        for (Formatting formatting: colors
        ) {
            Color color = new Color(formatting.getColorValue());

            HashMap<Integer, HashMap<Integer, Formatting>> reds;
            HashMap<Integer, Formatting> greens;

            if(!BRG.containsKey(color.getBlue())) {

                BRG.put(color.getBlue(), new HashMap<>());
            }
            reds = BRG.get(color.getBlue());

            if(!reds.containsKey(color.getRed())) {

                reds.put(color.getRed(), new HashMap<>());
            }
            greens = reds.get(color.getRed());


            if(!greens.containsKey(color.getGreen())) {

                greens.put(color.getGreen(), formatting);
            }

        }



    }



    public static Formatting getMCColor(Color color) {


        //return step(step(step(root,color.getRed()),color.getGreen()),color.getBlue()).getFormatting();


        if(color.getRed() >= color.getGreen() && color.getRed() >= color.getBlue()) {

            HashMap<Integer, HashMap<Integer, Formatting>> greens = RGB.get(calcKey(RGB.keySet(), color.getRed()));
            HashMap<Integer, Formatting> blues = greens.get(calcKey(greens.keySet(), color.getGreen()));

            return blues.get(calcKey(blues.keySet(), color.getBlue()));
        } else if(color.getGreen() >= color.getBlue() && color.getGreen() >= color.getRed()) {
            HashMap<Integer, HashMap<Integer, Formatting>> blues = GBR.get(calcKey(GBR.keySet(), color.getGreen()));
            HashMap<Integer, Formatting> reds = blues.get(calcKey(blues.keySet(), color.getBlue()));
            return reds.get(calcKey(reds.keySet(), color.getRed()));
        } else if(color.getBlue() >= color.getRed() && color.getBlue() >= color.getGreen()) {
            HashMap<Integer, HashMap<Integer, Formatting>> reds = BRG.get(calcKey(BRG.keySet(), color.getBlue()));
            HashMap<Integer, Formatting> greens = reds.get(calcKey(reds.keySet(), color.getRed()));
            return greens.get(calcKey(greens.keySet(), color.getGreen()));

        }

        System.out.println("NO COLOR FOUND ALARM");
        return null;
    }

    private static Integer calcKey(Set<Integer> keys, int value) {
        int distance = 1000;
        int tmp = 0;
        Integer key = 0;
        for (Integer i: keys
             ) {
            tmp = Math.abs(i - value);
            if(tmp < distance) {
                distance = tmp;
                key = i;
            } else {
                break;
            }
        }

        return key;

    }


    public static Formatting getColor(Color color) {

        Formatting f = colorMap[Math.round(color.getRed() /85)][Math.round(color.getGreen() /85)][Math.round(color.getBlue() /85)];

        return f==null?Formatting.GRAY:f;

    }
}
