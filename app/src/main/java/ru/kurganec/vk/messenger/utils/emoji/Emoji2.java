package ru.kurganec.vk.messenger.utils.emoji;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import ru.kurganec.vk.messenger.R;
import ru.kurganec.vk.messenger.model.VK;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Emoji2 {

//     final int length = s.length();
// for (int offset = 0; offset < length; ) {
//    final int codepoint = s.codePointAt(offset);

//    // do something with the codepoint

//    offset += Character.charCount(codepoint);
// }

    private static final int[] ROW_SIZES = {27, 29, 33, 34, 34};

    public static long[][] data = {
            {3627933188L, 3627933187L, 3627933184L, 3627933194L, 9786L, 3627933193L, 3627933197L, 3627933208L, 3627933210L, 3627933207L, 3627933209L, 3627933212L, 3627933213L, 3627933211L, 3627933235L, 3627933185L, 3627933204L, 3627933196L, 3627933202L, 3627933214L, 3627933219L, 3627933218L, 3627933186L, 3627933229L, 3627933226L, 3627933221L, 3627933232L, 3627933189L, 3627933203L, 3627933225L, 3627933227L, 3627933224L, 3627933233L, 3627933216L, 3627933217L, 3627933220L, 3627933206L, 3627933190L, 3627933195L, 3627933239L, 3627933198L, 3627933236L, 3627933237L, 3627933234L, 3627933215L, 3627933222L, 3627933223L, 3627933192L, 3627932799L, 3627933230L, 3627933228L, 3627933200L, 3627933205L, 3627933231L, 3627933238L, 3627933191L, 3627933199L, 3627933201L, 3627932786L, 3627932787L, 3627932782L, 3627932791L, 3627932802L, 3627932790L, 3627932774L, 3627932775L, 3627932776L, 3627932777L, 3627932788L, 3627932789L, 3627932785L, 3627932796L, 3627932792L, 3627933242L, 3627933240L, 3627933243L, 3627933245L, 3627933244L, 3627933248L, 3627933247L, 3627933241L, 3627933246L, 3627932793L, 3627932794L, 3627933256L, 3627933257L, 3627933258L, 3627932800L, 3627932797L, 3627932841L, 3627932965L, 10024L, 3627867935L, 3627932843L, 3627932837L, 3627932834L, 3627932838L, 3627932839L, 3627932836L, 3627932840L, 3627932738L, 3627932736L, 3627932739L, 3627932741L, 3627932740L, 3627932749L, 3627932750L, 3627932748L, 3627932746L, 9994L, 9996L, 3627932747L, 9995L, 3627932752L, 3627932742L, 3627932743L, 3627932745L, 3627932744L, 3627933260L, 3627933263L, 9757L, 3627932751L, 3627932842L, 3627933366L, 3627868099L, 3627932803L, 3627932779L, 3627932778L, 3627932780L, 3627932781L, 3627932815L, 3627932817L, 3627932783L, 3627933254L, 3627933253L, 3627932801L, 3627933259L, 3627932806L, 3627932807L, 3627932805L, 3627932784L, 3627933262L, 3627933261L, 3627933255L, 3627868073L, 3627932753L, 3627932754L, 3627932767L, 3627932766L, 3627932769L, 3627932768L, 3627932770L, 3627932757L, 3627932756L, 3627932762L, 3627932759L, 3627868093L, 3627932758L, 3627932760L, 3627932761L, 3627932860L, 3627932764L, 3627932765L, 3627932763L, 3627932755L, 3627868032L, 3627867906L, 3627932804L, 3627932827L, 3627932825L, 3627932828L, 3627932826L, 10084L, 3627932820L, 3627932823L, 3627932819L, 3627932821L, 3627932822L, 3627932830L, 3627932824L, 3627932812L, 3627932811L, 3627932813L, 3627932814L, 3627932772L, 3627932773L, 3627932844L, 3627932771L, 3627932845L},
            {3627932726L, 3627932730L, 3627932721L, 3627932717L, 3627932729L, 3627932720L, 3627932728L, 3627932719L, 3627932712L, 3627932731L, 3627932727L, 3627932733L, 3627932718L, 3627932695L, 3627932725L, 3627932690L, 3627932724L, 3627932689L, 3627932696L, 3627932732L, 3627932711L, 3627932710L, 3627932708L, 3627932709L, 3627932707L, 3627932692L, 3627932685L, 3627932706L, 3627932699L, 3627932701L, 3627932700L, 3627932702L, 3627932684L, 3627932697L, 3627932698L, 3627932704L, 3627932703L, 3627932716L, 3627932723L, 3627932683L, 3627932676L, 3627932687L, 3627932672L, 3627932675L, 3627932677L, 3627932679L, 3627932681L, 3627932686L, 3627932688L, 3627932691L, 3627932693L, 3627932694L, 3627932673L, 3627932674L, 3627932722L, 3627932705L, 3627932682L, 3627932715L, 3627932714L, 3627932678L, 3627932680L, 3627932713L, 3627932734L, 3627932816L, 3627867960L, 3627867959L, 3627867968L, 3627867961L, 3627867963L, 3627867962L, 3627867969L, 3627867971L, 3627867970L, 3627867967L, 3627867966L, 3627867972L, 3627867957L, 3627867956L, 3627867954L, 3627867955L, 3627867952L, 3627867953L, 3627867964L, 3627867920L, 3627867934L, 3627867933L, 3627867930L, 3627867921L, 3627867922L, 3627867923L, 3627867924L, 3627867925L, 3627867926L, 3627867927L, 3627867928L, 3627867932L, 3627867931L, 3627867929L, 3627867917L, 3627867918L, 3627867919L, 3627867915L, 3627867916L, 3627867936L, 11088L, 9728L, 9925L, 9729L, 9889L, 9748L, 10052L, 9924L, 3627867904L, 3627867905L, 3627867912L, 3627867914L},
            {3627868045L, 3627932829L, 3627868046L, 3627868050L, 3627868051L, 3627868047L, 3627868038L, 3627868039L, 3627868048L, 3627868049L, 3627868035L, 3627932795L, 3627868037L, 3627868036L, 3627868033L, 3627868043L, 3627868041L, 3627868042L, 3627868040L, 3627868044L, 3627932974L, 3627868069L, 3627932919L, 3627932921L, 3627932924L, 3627932863L, 3627932864L, 3627932861L, 3627932862L, 3627932859L, 3627932913L, 9742L, 3627932894L, 3627932895L, 3627932896L, 3627932897L, 3627932922L, 3627932923L, 3627932938L, 3627932937L, 3627932936L, 3627932935L, 3627932948L, 3627932948L, 3627932898L, 3627932899L, 9203L, 8987L, 9200L, 8986L, 3627932947L, 3627932946L, 3627932943L, 3627932944L, 3627932945L, 3627932942L, 3627932833L, 3627932966L, 3627932934L, 3627932933L, 3627932940L, 3627932939L, 3627932941L, 3627933376L, 3627933375L, 3627933373L, 3627932967L, 3627932969L, 3627932968L, 3627933354L, 3627933356L, 3627932835L, 3627932971L, 3627932970L, 3627932810L, 3627932809L, 3627932848L, 3627932852L, 3627932853L, 3627932855L, 3627932854L, 3627932851L, 3627932856L, 3627932914L, 3627932903L, 3627932901L, 3627932900L, 9993L, 3627932905L, 3627932904L, 3627932911L, 3627932907L, 3627932906L, 3627932908L, 3627932909L, 3627932910L, 3627932902L, 3627932893L, 3627932868L, 3627932867L, 3627932881L, 3627932874L, 3627932872L, 3627932873L, 3627932892L, 3627932875L, 3627932869L, 3627932870L, 3627932871L, 3627932865L, 3627932866L, 9986L, 3627932876L, 3627932878L, 10002L, 9999L, 3627932879L, 3627932880L, 3627932885L, 3627932887L, 3627932888L, 3627932889L, 3627932883L, 3627932884L, 3627932882L, 3627932890L, 3627932886L, 3627932950L, 3627932891L, 3627932972L, 3627932973L, 3627932912L, 3627868072L, 3627868076L, 3627868068L, 3627868071L, 3627868092L, 3627868085L, 3627868086L, 3627868089L, 3627868091L, 3627868090L, 3627868087L, 3627868088L, 3627932798L, 3627868078L, 3627867343L, 3627868084L, 3627867140L, 3627868082L, 3627868079L, 3627868104L, 3627868096L, 9917L, 9918L, 3627868094L, 3627868081L, 3627868105L, 3627868083L, 9971L, 3627933365L, 3627933364L, 3627868097L, 3627868103L, 3627868102L, 3627868095L, 3627868098L, 3627868106L, 3627868100L, 3627868067L, 9749L, 3627868021L, 3627868022L, 3627868028L, 3627868026L, 3627868027L, 3627868024L, 3627868025L, 3627868023L, 3627868020L, 3627867989L, 3627867988L, 3627867999L, 3627867991L, 3627867990L, 3627867997L, 3627867995L, 3627868004L, 3627868017L, 3627868003L, 3627868005L, 3627867993L, 3627867992L, 3627867994L, 3627867996L, 3627868018L, 3627868002L, 3627868001L, 3627868019L, 3627867998L, 3627868009L, 3627868014L, 3627868006L, 3627868008L, 3627868007L, 3627868034L, 3627868016L, 3627868010L, 3627868011L, 3627868012L, 3627868013L, 3627868015L, 3627867982L, 3627867983L, 3627867978L, 3627867979L, 3627867986L, 3627867975L, 3627867977L, 3627867987L, 3627867985L, 3627867976L, 3627867980L, 3627867984L, 3627867981L, 3627868000L, 3627867974L, 3627867973L, 3627867965L},
            {3627868128L, 3627868129L, 3627868139L, 3627868130L, 3627868131L, 3627868133L, 3627868134L, 3627868138L, 3627868137L, 3627868136L, 3627932818L, 9962L, 3627868140L, 3627868132L, 3627867911L, 3627867910L, 3627868143L, 3627868144L, 9978L, 3627868141L, 3627933180L, 3627933182L, 3627933179L, 3627867908L, 3627867909L, 3627867907L, 3627933181L, 3627867913L, 3627868064L, 3627868065L, 9970L, 3627868066L, 3627933346L, 9973L, 3627933348L, 3627933347L, 9875L, 3627933312L, 9992L, 3627932858L, 3627933313L, 3627933314L, 3627933322L, 3627933321L, 3627933342L, 3627933318L, 3627933316L, 3627933317L, 3627933320L, 3627933319L, 3627933341L, 3627933323L, 3627933315L, 3627933326L, 3627933324L, 3627933325L, 3627933337L, 3627933336L, 3627933335L, 3627933333L, 3627933334L, 3627933339L, 3627933338L, 3627933352L, 3627933331L, 3627933332L, 3627933330L, 3627933329L, 3627933328L, 3627933362L, 3627933345L, 3627933343L, 3627933344L, 3627933340L, 3627932808L, 3627933327L, 3627868075L, 3627933350L, 3627933349L, 9888L, 3627933351L, 3627932976L, 9981L, 3627868142L, 3627868080L, 9832L, 3627933183L, 3627868074L, 3627868077L, 3627932877L, 3627933353L, -2865171240719688203L, -2865171236424720905L, -2865171266489491990L, -2865171270784459277L, -2865171193475047944L, -2865171257899557385L, -2865171262194524680L, -2865171245014655495L, -2865171206359949830L, -2865171253604590105L},
            {3219683L, 3285219L, 3350755L, 3416291L, 3481827L, 3547363L, 3612899L, 3678435L, 3743971L, 3154147L, 3627932959L, 3627932962L, 2302179L, 3627932963L, 11014L, 11015L, 11013L, 10145L, 3627932960L, 3627932961L, 3627932964L, 8599L, 8598L, 8600L, 8601L, 8596L, 8597L, 3627932932L, 9664L, 9654L, 3627932988L, 3627932989L, 8617L, 8618L, 8505L, 9194L, 9193L, 9195L, 9196L, 10549L, 10548L, 3627867543L, 3627932928L, 3627932929L, 3627932930L, 3627867541L, 3627867545L, 3627867538L, 3627867539L, 3627867542L, 3627932918L, 3627868070L, 3627867649L, 3627867695L, 3627867699L, 3627867701L, 3627867698L, 3627867700L, 3627867698L, 3627867728L, 3627867705L, 3627867706L, 3627867702L, 3627867674L, 3627933371L, 3627933369L, 3627933370L, 3627933372L, 3627933374L, 3627933360L, 3627933358L, 3627867519L, 9855L, 3627933357L, 3627867703L, 3627867704L, 3627867650L, 9410L, 3627867729L, 12953L, 12951L, 3627867537L, 3627867544L, 3627867540L, 3627933355L, 3627932958L, 3627932917L, 3627933359L, 3627933361L, 3627933363L, 3627933367L, 3627933368L, 9940L, 10035L, 10055L, 10062L, 9989L, 10036L, 3627932831L, 3627867546L, 3627932915L, 3627932916L, 3627867504L, 3627867505L, 3627867534L, 3627867518L, 3627932832L, 10175L, 9851L, 9800L, 9801L, 9802L, 9803L, 9804L, 9805L, 9806L, 9807L, 9808L, 9809L, 9810L, 9811L, 9934L, 3627932975L, 3627868135L, 3627932857L, 3627932850L, 3627932849L, 169L, 174L, 8482L, 12349L, 12336L, 3627932957L, 3627932954L, 3627932953L, 3627932955L, 3627932956L, 10060L, 11093L, 10071L, 10067L, 10069L, 10068L, 3627932931L, 3627933019L, 3627933031L, 3627933008L, 3627933020L, 3627933009L, 3627933021L, 3627933010L, 3627933022L, 3627933011L, 3627933023L, 3627933012L, 3627933024L, 3627933013L, 3627933014L, 3627933015L, 3627933016L, 3627933017L, 3627933018L, 3627933025L, 3627933026L, 3627933027L, 3627933028L, 3627933029L, 3627933030L, 10006L, 10133L, 10134L, 10135L, 9824L, 9829L, 9827L, 9830L, 3627932846L, 3627932847L, 10004L, 9745L, 3627932952L, 3627932951L, 10160L, 3627932977L, 3627932978L, 3627932979L, 9724L, 9723L, 9726L, 9725L, 9642L, 9643L, 3627932986L, 11036L, 11035L, 9899L, 9898L, 3627932980L, 3627932981L, 3627932987L, 3627932982L, 3627932983L, 3627932984L, 3627932985L}
    };


    private static final char emojiChars[] = {
            '\u203C', '\u2049', '\u2139', '\u2194', '\u2195', '\u2196', '\u2197', '\u2198', '\u2199', '\u21A9',
            '\u21AA', '\u231A', '\u231B', '\u23E9', '\u23EA', '\u23EB', '\u23EC', '\u23F0', '\u23F3', '\u24C2',
            '\u25AA', '\u25AB', '\u25B6', '\u25C0', '\u25FB', '\u25FC', '\u25FD', '\u25FE', '\u2600', '\u2601',
            '\u260E', '\u2611', '\u2614', '\u2615', '\u261D', '\u263A', '\u2648', '\u2649', '\u264A', '\u264B',
            '\u264C', '\u264D', '\u264E', '\u264F', '\u2650', '\u2651', '\u2652', '\u2653', '\u2660', '\u2663',
            '\u2665', '\u2666', '\u2668', '\u267B', '\u267F', '\u2693', '\u26A0', '\u26A1', '\u26AA', '\u26AB',
            '\u26BD', '\u26BE', '\u26C4', '\u26C5', '\u26CE', '\u26D4', '\u26EA', '\u26F2', '\u26F3', '\u26F5',
            '\u26FA', '\u26FD', '\u2702', '\u2705', '\u2708', '\u2709', '\u270A', '\u270B', '\u270C', '\u270F',
            '\u2712', '\u2714', '\u2716', '\u2728', '\u2733', '\u2734', '\u2744', '\u2747', '\u274C', '\u274E',
            '\u2753', '\u2754', '\u2755', '\u2757', '\u2764', '\u2795', '\u2796', '\u2797', '\u27A1', '\u27B0',
            '\u27BF', '\u2934', '\u2935', '\u2B05', '\u2B06', '\u2B07', '\u2B1B', '\u2B1C', '\u2B50', '\u2B55',
            '\u3030', '\u303D', '\u3297', '\u3299'
    };

    private static final HashMap<Long, EmojiDrawable> smallEmoji = new HashMap<>();
    public static final Map<Long, EmojiDrawable> bigEmoji = new HashMap<>();

    public static void load() {
        Resources res = VK.inst().getResources();
        int textSize = res.getDimensionPixelSize(R.dimen.emoji_text_size);
        for (int page = 0; page < data.length; page++) {
            Future<Bitmap> bmp = loadPage(page);
            int rowSize = ROW_SIZES[page];
            int squareSize = 56;
            int densityDpi = res.getDisplayMetrics().densityDpi;
            if (densityDpi < DisplayMetrics.DENSITY_TV) {
                squareSize /= 2;
            }
            squareSize *= getScale(densityDpi);
            for (int numberInPage = 0; numberInPage < data[page].length; numberInPage++) {
                int column = numberInPage % rowSize;
                int row = numberInPage / rowSize;
                Rect r = new Rect(column * squareSize, row * squareSize, (column + 1) * squareSize, (row + 1) * squareSize);
                long id = data[page][numberInPage];
                smallEmoji.put(id, new EmojiDrawable(id, r, bmp, textSize));
                bigEmoji.put(id, new EmojiDrawable(id, r, bmp, squareSize));
            }
        }

    }

    private static final ExecutorService s = Executors.newFixedThreadPool(2);
    private static Future<Bitmap> loadPage(final int page) {
        return s.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                try {
                    VK ctx = VK.inst();
                    int screenDensity = ctx.getResources().getDisplayMetrics().densityDpi;

                    BitmapFactory.Options localOptions = new BitmapFactory.Options();
                    localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    localOptions.inDither = true;
                    if (screenDensity < DisplayMetrics.DENSITY_TV)
                        localOptions.inSampleSize = 2;
                    Log.i("vk", "Load emoji page " + page);
                    InputStream colorStream = ctx.getAssets().open("emoji" + page + "c.jpg");
                    Bitmap colorBitmap = BitmapFactory.decodeStream(colorStream, null, localOptions);
                    colorStream.close();
                    InputStream alphaStream = ctx.getAssets().open("emoji" + page + "a.jpg");
                    Bitmap alphaBitmap = BitmapFactory.decodeStream(alphaStream, null, localOptions);
                    alphaStream.close();
                    int width = colorBitmap.getWidth();
                    int height = colorBitmap.getHeight();
                    Log.i("vk", "EMOJI INIT: c: " + width + "x" + height + ", a: " + alphaBitmap.getWidth() + "x" + alphaBitmap.getHeight());
                    int[] arrayOfInt1 = new int[width * height];
                    colorBitmap.getPixels(arrayOfInt1, 0, width, 0, 0, width, height);
                    colorBitmap.recycle();
                    System.gc();
                    int[] arrayOfInt2 = new int[width * height];
                    alphaBitmap.getPixels(arrayOfInt2, 0, width, 0, 0, width, height);
                    alphaBitmap.recycle();
                    for (int m = 0; ; m++) { //todo renderscipt?
                        int n = arrayOfInt1.length;
                        if (m >= n)
                            break;
                        arrayOfInt1[m] = (0xFFFFFF & arrayOfInt1[m] | arrayOfInt2[m] << 24);
                    }
                    Bitmap result = Bitmap.createBitmap(width, height, localOptions.inPreferredConfig);
                    result.setPixels(arrayOfInt1, 0, width, 0, 0, width, height);

                    float scale = getScale(screenDensity);
                    if (scale != 1f) {
                        Bitmap downscaled = Bitmap.createScaledBitmap(result, (int) (width * scale), (int) (height * scale), true);
                        result.recycle();
                        System.gc();
                        return downscaled;
                    } else {
                        return result;
                    }
                } catch (Throwable localThrowable) {
                    Log.e("vk", "Error loading emoji", localThrowable);
                    return null;
                }
            }
        });

    }

    private static float getScale(int screenDensity) {
        switch (screenDensity) {
            case DisplayMetrics.DENSITY_LOW:
            case DisplayMetrics.DENSITY_HIGH:
            case DisplayMetrics.DENSITY_TV:
                return 3f / 4f;
            default:
                return 1f;
        }
    }


    public static CharSequence replaceEmoji(CharSequence charsequence) {
        Spannable obj;
        if (charsequence == null || charsequence.length() == 0) {
            return charsequence;
        } else {
            long l;
            int i;
            if (charsequence instanceof Spannable) {
                obj = (Spannable) charsequence;
            } else {
                obj = android.text.Spannable.Factory.getInstance().newSpannable(charsequence);
            }
            l = 0L;
            i = 0;
            while (i < charsequence.length()) {
                int j = charsequence.charAt(i);
                if (j == 55356 || j == 55357 || l != 0L && (0xffffffff00000000L & l) == 0L && j >= 56806 && j <= 56826) {
                    l = l << 16 | (long) j;
                } else if (l > 0L && (0xf000 & j) == 53248) {
                    long l1 = l << 16 | (long) j;
                    android.graphics.drawable.Drawable drawable2 = smallEmoji.get(l1);//Emoji.getEmojiDrawable(l1);
                    if (drawable2 != null) {
                        ImageSpan ximagespan = new ImageSpan(drawable2, 0);
                        if (j >= 56806 && j <= 56826) {
                            ((Spannable) (obj)).setSpan(ximagespan, i - 3, i + 1, 0);
                        } else {
                            ((Spannable) (obj)).setSpan(ximagespan, i - 1, i + 1, 0);
                        }
                    }
                    l = 0L;
                } else if (j == 8419) {
                    if (i > 0) {
                        int k = charsequence.charAt(i - 1);
                        if (k >= 48 && k <= 57 || k == 35) {
                            long l1 = (long) k << 16 | (long) j;
                            android.graphics.drawable.Drawable drawable1 = smallEmoji.get(l1);
                            if (drawable1 != null) {
                                ((Spannable) (obj)).setSpan(new ImageSpan(drawable1, 0), i - 1, i + 1, 0);
                            }
                            l = 0L;
                        }
                    }
                } else if (inArray((char) j, emojiChars)) {
                    android.graphics.drawable.Drawable drawable = smallEmoji.get(Long.valueOf(j));
                    if (drawable != null) {
                        ((Spannable) (obj)).setSpan(new ImageSpan(drawable, 0), i, i + 1, 0);
                    }
                }
                i++;
            }
        }
        return ((CharSequence) (obj));
    }

    public static class EmojiDrawable extends Drawable {
        private static final Paint p = new Paint();
        final long id;
        final Rect rect;
        final Future<Bitmap> bmp;
        private final int bounds;

        private EmojiDrawable(long id, Rect rect, Future<Bitmap> bmp, int bounds) {
            this.id = id;
            this.rect = rect;
            this.bmp = bmp;
            this.bounds = bounds;
            setBounds(0, 0, bounds, bounds);
        }

        @Override
        public void draw(Canvas canvas) {
            try {
                Bitmap bitmap = bmp.get();
                if (bitmap != null){
                    canvas.drawBitmap(bitmap, rect, getBounds(), p);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }


        @Override
        public int getIntrinsicWidth() {
            return bounds;
        }

        @Override
        public int getIntrinsicHeight() {
            return bounds;
        }

        public Drawable copy() {
            return new EmojiDrawable(id, rect, bmp,bounds );
        }
    }

    private static boolean inArray(char c, char ac[]) {
        for (char anAc : ac) {
            if (anAc == c) {
                return true;
            }
        }
        return false;
    }
}
