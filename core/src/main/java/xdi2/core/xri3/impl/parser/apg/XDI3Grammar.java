package xdi2.core.xri3.impl.parser.apg;
import com.coasttocoastresearch.apg.Grammar;
import java.io.PrintStream;

public class XDI3Grammar extends Grammar{

    // public API
    public static Grammar getInstance(){
        if(factoryInstance == null){
            factoryInstance = new XDI3Grammar(getRules(), getUdts(), getOpcodes());
        }
        return factoryInstance;
    }

    // rule name enum
    public static int ruleCount = 91;
    public enum RuleNames{
        ALPHA("ALPHA", 75, 476, 3),
        BIT("BIT", 76, 479, 3),
        CHAR("CHAR", 77, 482, 1),
        CR("CR", 78, 483, 1),
        CRLF("CRLF", 79, 484, 3),
        CTL("CTL", 80, 487, 3),
        DEC_OCTET("dec-octet", 55, 367, 16),
        DIGIT("DIGIT", 81, 490, 1),
        DQUOTE("DQUOTE", 82, 491, 1),
        GCS_CHAR("gcs-char", 23, 100, 5),
        GEN_DELIMS("gen-delims", 72, 451, 8),
        GLOBAL_SUBSEG("global-subseg", 21, 87, 7),
        H16("h16", 53, 357, 2),
        HEXDIG("HEXDIG", 83, 492, 8),
        HTAB("HTAB", 84, 500, 1),
        IAUTHORITY("iauthority", 46, 223, 10),
        IFRAGMENT("ifragment", 66, 421, 5),
        IHIER_PART("ihier-part", 45, 215, 8),
        IHOST("ihost", 48, 239, 4),
        IP_LITERAL("IP-literal", 49, 243, 6),
        IPATH_ABEMPTY("ipath-abempty", 58, 390, 4),
        IPATH_ABS("ipath-abs", 59, 394, 9),
        IPATH_EMPTY("ipath-empty", 61, 409, 1),
        IPATH_ROOTLESS("ipath-rootless", 60, 403, 6),
        IPCHAR("ipchar", 67, 426, 6),
        IPRIVATE("iprivate", 65, 420, 1),
        IPV4ADDRESS("IPv4address", 54, 359, 8),
        IPV6ADDRESS("IPv6address", 51, 259, 92),
        IPVFUTURE("IPvFuture", 50, 249, 10),
        IQUERY("iquery", 64, 414, 6),
        IREG_NAME("ireg-name", 56, 383, 5),
        IRI("IRI", 43, 194, 12),
        ISEGMENT("isegment", 62, 410, 2),
        ISEGMENT_NZ("isegment-nz", 63, 412, 2),
        IUNRESERVED("iunreserved", 68, 432, 8),
        IUSERINFO("iuserinfo", 47, 233, 6),
        LCS_CHAR("lcs-char", 24, 105, 3),
        LF("LF", 85, 501, 1),
        LITERAL("literal", 25, 108, 2),
        LITERAL_NC("literal-nc", 26, 110, 2),
        LOCAL_SUBSEG("local-subseg", 22, 94, 6),
        LS32("ls32", 52, 351, 6),
        LWSP("LWSP", 86, 502, 6),
        OCTET("OCTET", 87, 508, 1),
        PCT_ENCODED("pct-encoded", 69, 440, 4),
        PORT("port", 57, 388, 2),
        RELATIVE_XRI_PART("relative-xri-part", 17, 72, 4),
        RELATIVE_XRI_REF("relative-xri-ref", 16, 62, 10),
        RESERVED("reserved", 71, 448, 3),
        SCHEME("scheme", 44, 206, 9),
        SP("SP", 88, 509, 1),
        SUB_DELIMS("sub-delims", 73, 459, 10),
        SUBSEG("subseg", 20, 83, 4),
        UCSCHAR("ucschar", 70, 444, 4),
        UNRESERVED("unreserved", 74, 469, 7),
        VCHAR("VCHAR", 89, 510, 1),
        WSP("WSP", 90, 511, 3),
        XDI_ADDRESS("xdi-address", 0, 0, 3),
        XDI_CONTEXT("xdi-context", 1, 3, 1),
        XDI_GLOBAL_SUBSEG("xdi-global-subseg", 8, 23, 7),
        XDI_LOCAL_SUBSEG("xdi-local-subseg", 9, 30, 6),
        XDI_OBJECT("xdi-object", 5, 12, 1),
        XDI_PREDICATE("xdi-predicate", 4, 11, 1),
        XDI_SEGMENT("xdi-segment", 6, 13, 6),
        XDI_STATEMENT("xdi-statement", 2, 4, 6),
        XDI_SUBJECT("xdi-subject", 3, 10, 1),
        XDI_SUBSEG("xdi-subseg", 7, 19, 4),
        XDI_XREF("xdi-xref", 10, 36, 4),
        XDI_XREF_ADDRESS("xdi-xref-address", 13, 45, 4),
        XDI_XREF_EMPTY("xdi-xref-empty", 11, 40, 1),
        XDI_XREF_IRI("xdi-xref-IRI", 12, 41, 4),
        XREF("xref", 27, 112, 4),
        XREF_EMPTY("xref-empty", 28, 116, 1),
        XREF_IRI("xref-IRI", 30, 121, 4),
        XREF_XRI_REFERENCE("xref-xri-reference", 29, 117, 4),
        XRI("xri", 14, 49, 10),
        XRI_AUTHORITY("xri-authority", 19, 79, 4),
        XRI_GEN_DELIMS("xri-gen-delims", 41, 178, 11),
        XRI_HIER_PART("xri-hier-part", 18, 76, 3),
        XRI_PATH("xri-path", 31, 125, 5),
        XRI_PATH_ABEMPTY("xri-path-abempty", 32, 130, 4),
        XRI_PATH_ABS("xri-path-abs", 33, 134, 9),
        XRI_PATH_NOSCHEME("xri-path-noscheme", 34, 143, 6),
        XRI_PCHAR("xri-pchar", 38, 166, 5),
        XRI_PCHAR_NC("xri-pchar-nc", 39, 171, 4),
        XRI_REFERENCE("xri-reference", 15, 59, 3),
        XRI_RESERVED("xri-reserved", 40, 175, 3),
        XRI_SEGMENT("xri-segment", 35, 149, 5),
        XRI_SEGMENT_NC("xri-segment-nc", 37, 160, 6),
        XRI_SEGMENT_NZ("xri-segment-nz", 36, 154, 6),
        XRI_SUB_DELIMS("xri-sub-delims", 42, 189, 5);
        private String name;
        private int id;
        private int offset;
        private int count;
        RuleNames(String string, int id, int offset, int count){
            this.name = string;
            this.id = id;
            this.offset = offset;
            this.count = count;
        }
        public  String ruleName(){return name;}
        public  int    ruleID(){return id;}
        private int    opcodeOffset(){return offset;}
        private int    opcodeCount(){return count;}
    }

    // UDT name enum
    public static int udtCount = 0;
    public enum UdtNames{
    }

    // private
    private static XDI3Grammar factoryInstance = null;
    private XDI3Grammar(Rule[] rules, Udt[] udts, Opcode[] opcodes){
        super(rules, udts, opcodes);
    }

    private static Rule[] getRules(){
    	Rule[] rules = new Rule[91];
        for(RuleNames r : RuleNames.values()){
            rules[r.ruleID()] = getRule(r.ruleID(), r.ruleName(), r.opcodeOffset(), r.opcodeCount());
        }
        return rules;
    }

    private static Udt[] getUdts(){
    	Udt[] udts = new Udt[0];
        return udts;
    }

        // opcodes
    private static Opcode[] getOpcodes(){
    	Opcode[] op = new Opcode[514];
        {int[] a = {1,2}; op[0] = getOpcodeAlt(a);}
        op[1] = getOpcodeRnm(2, 4); // xdi-statement
        op[2] = getOpcodeRnm(1, 3); // xdi-context
        op[3] = getOpcodeRnm(6, 13); // xdi-segment
        {int[] a = {5,6,7,8,9}; op[4] = getOpcodeCat(a);}
        op[5] = getOpcodeRnm(3, 10); // xdi-subject
        {char[] a = {47}; op[6] = getOpcodeTls(a);}
        op[7] = getOpcodeRnm(4, 11); // xdi-predicate
        {char[] a = {47}; op[8] = getOpcodeTls(a);}
        op[9] = getOpcodeRnm(5, 12); // xdi-object
        op[10] = getOpcodeRnm(6, 13); // xdi-segment
        op[11] = getOpcodeRnm(6, 13); // xdi-segment
        op[12] = getOpcodeRnm(6, 13); // xdi-segment
        {int[] a = {14,17}; op[13] = getOpcodeCat(a);}
        {int[] a = {15,16}; op[14] = getOpcodeAlt(a);}
        op[15] = getOpcodeRnm(25, 108); // literal
        op[16] = getOpcodeRnm(7, 19); // xdi-subseg
        op[17] = getOpcodeRep((char)0, Character.MAX_VALUE, 18);
        op[18] = getOpcodeRnm(7, 19); // xdi-subseg
        {int[] a = {20,21,22}; op[19] = getOpcodeAlt(a);}
        op[20] = getOpcodeRnm(8, 23); // xdi-global-subseg
        op[21] = getOpcodeRnm(9, 30); // xdi-local-subseg
        op[22] = getOpcodeRnm(10, 36); // xdi-xref
        {int[] a = {24,25}; op[23] = getOpcodeCat(a);}
        op[24] = getOpcodeRnm(23, 100); // gcs-char
        op[25] = getOpcodeRep((char)0, (char)1, 26);
        {int[] a = {27,28,29}; op[26] = getOpcodeAlt(a);}
        op[27] = getOpcodeRnm(9, 30); // xdi-local-subseg
        op[28] = getOpcodeRnm(10, 36); // xdi-xref
        op[29] = getOpcodeRnm(25, 108); // literal
        {int[] a = {31,32}; op[30] = getOpcodeCat(a);}
        op[31] = getOpcodeRnm(24, 105); // lcs-char
        op[32] = getOpcodeRep((char)0, (char)1, 33);
        {int[] a = {34,35}; op[33] = getOpcodeAlt(a);}
        op[34] = getOpcodeRnm(10, 36); // xdi-xref
        op[35] = getOpcodeRnm(25, 108); // literal
        {int[] a = {37,38,39}; op[36] = getOpcodeAlt(a);}
        op[37] = getOpcodeRnm(11, 40); // xdi-xref-empty
        op[38] = getOpcodeRnm(12, 41); // xdi-xref-IRI
        op[39] = getOpcodeRnm(13, 45); // xdi-xref-address
        {char[] a = {40,41}; op[40] = getOpcodeTls(a);}
        {int[] a = {42,43,44}; op[41] = getOpcodeCat(a);}
        {char[] a = {40}; op[42] = getOpcodeTls(a);}
        op[43] = getOpcodeRnm(43, 194); // IRI
        {char[] a = {41}; op[44] = getOpcodeTls(a);}
        {int[] a = {46,47,48}; op[45] = getOpcodeCat(a);}
        {char[] a = {40}; op[46] = getOpcodeTls(a);}
        op[47] = getOpcodeRnm(0, 0); // xdi-address
        {char[] a = {41}; op[48] = getOpcodeTls(a);}
        {int[] a = {50,51,55}; op[49] = getOpcodeCat(a);}
        op[50] = getOpcodeRnm(18, 76); // xri-hier-part
        op[51] = getOpcodeRep((char)0, (char)1, 52);
        {int[] a = {53,54}; op[52] = getOpcodeCat(a);}
        {char[] a = {63}; op[53] = getOpcodeTls(a);}
        op[54] = getOpcodeRnm(64, 414); // iquery
        op[55] = getOpcodeRep((char)0, (char)1, 56);
        {int[] a = {57,58}; op[56] = getOpcodeCat(a);}
        {char[] a = {59}; op[57] = getOpcodeTls(a);}
        op[58] = getOpcodeRnm(66, 421); // ifragment
        {int[] a = {60,61}; op[59] = getOpcodeAlt(a);}
        op[60] = getOpcodeRnm(14, 49); // xri
        op[61] = getOpcodeRnm(16, 62); // relative-xri-ref
        {int[] a = {63,64,68}; op[62] = getOpcodeCat(a);}
        op[63] = getOpcodeRnm(17, 72); // relative-xri-part
        op[64] = getOpcodeRep((char)0, (char)1, 65);
        {int[] a = {66,67}; op[65] = getOpcodeCat(a);}
        {char[] a = {63}; op[66] = getOpcodeTls(a);}
        op[67] = getOpcodeRnm(64, 414); // iquery
        op[68] = getOpcodeRep((char)0, (char)1, 69);
        {int[] a = {70,71}; op[69] = getOpcodeCat(a);}
        {char[] a = {59}; op[70] = getOpcodeTls(a);}
        op[71] = getOpcodeRnm(66, 421); // ifragment
        {int[] a = {73,74,75}; op[72] = getOpcodeAlt(a);}
        op[73] = getOpcodeRnm(33, 134); // xri-path-abs
        op[74] = getOpcodeRnm(34, 143); // xri-path-noscheme
        op[75] = getOpcodeRnm(61, 409); // ipath-empty
        {int[] a = {77,78}; op[76] = getOpcodeCat(a);}
        op[77] = getOpcodeRnm(19, 79); // xri-authority
        op[78] = getOpcodeRnm(32, 130); // xri-path-abempty
        {int[] a = {80,81}; op[79] = getOpcodeCat(a);}
        op[80] = getOpcodeRnm(21, 87); // global-subseg
        op[81] = getOpcodeRep((char)0, Character.MAX_VALUE, 82);
        op[82] = getOpcodeRnm(20, 83); // subseg
        {int[] a = {84,85,86}; op[83] = getOpcodeAlt(a);}
        op[84] = getOpcodeRnm(21, 87); // global-subseg
        op[85] = getOpcodeRnm(22, 94); // local-subseg
        op[86] = getOpcodeRnm(27, 112); // xref
        {int[] a = {88,89}; op[87] = getOpcodeCat(a);}
        op[88] = getOpcodeRnm(23, 100); // gcs-char
        op[89] = getOpcodeRep((char)0, (char)1, 90);
        {int[] a = {91,92,93}; op[90] = getOpcodeAlt(a);}
        op[91] = getOpcodeRnm(22, 94); // local-subseg
        op[92] = getOpcodeRnm(27, 112); // xref
        op[93] = getOpcodeRnm(25, 108); // literal
        {int[] a = {95,96}; op[94] = getOpcodeCat(a);}
        op[95] = getOpcodeRnm(24, 105); // lcs-char
        op[96] = getOpcodeRep((char)0, (char)1, 97);
        {int[] a = {98,99}; op[97] = getOpcodeAlt(a);}
        op[98] = getOpcodeRnm(27, 112); // xref
        op[99] = getOpcodeRnm(25, 108); // literal
        {int[] a = {101,102,103,104}; op[100] = getOpcodeAlt(a);}
        {char[] a = {61}; op[101] = getOpcodeTls(a);}
        {char[] a = {64}; op[102] = getOpcodeTls(a);}
        {char[] a = {43}; op[103] = getOpcodeTls(a);}
        {char[] a = {36}; op[104] = getOpcodeTls(a);}
        {int[] a = {106,107}; op[105] = getOpcodeAlt(a);}
        {char[] a = {42}; op[106] = getOpcodeTls(a);}
        {char[] a = {33}; op[107] = getOpcodeTls(a);}
        op[108] = getOpcodeRep((char)1, Character.MAX_VALUE, 109);
        op[109] = getOpcodeRnm(38, 166); // xri-pchar
        op[110] = getOpcodeRep((char)1, Character.MAX_VALUE, 111);
        op[111] = getOpcodeRnm(39, 171); // xri-pchar-nc
        {int[] a = {113,114,115}; op[112] = getOpcodeAlt(a);}
        op[113] = getOpcodeRnm(28, 116); // xref-empty
        op[114] = getOpcodeRnm(29, 117); // xref-xri-reference
        op[115] = getOpcodeRnm(30, 121); // xref-IRI
        {char[] a = {40,41}; op[116] = getOpcodeTls(a);}
        {int[] a = {118,119,120}; op[117] = getOpcodeCat(a);}
        {char[] a = {40}; op[118] = getOpcodeTls(a);}
        op[119] = getOpcodeRnm(15, 59); // xri-reference
        {char[] a = {41}; op[120] = getOpcodeTls(a);}
        {int[] a = {122,123,124}; op[121] = getOpcodeCat(a);}
        {char[] a = {40}; op[122] = getOpcodeTls(a);}
        op[123] = getOpcodeRnm(43, 194); // IRI
        {char[] a = {41}; op[124] = getOpcodeTls(a);}
        {int[] a = {126,127,128,129}; op[125] = getOpcodeAlt(a);}
        op[126] = getOpcodeRnm(32, 130); // xri-path-abempty
        op[127] = getOpcodeRnm(33, 134); // xri-path-abs
        op[128] = getOpcodeRnm(34, 143); // xri-path-noscheme
        op[129] = getOpcodeRnm(61, 409); // ipath-empty
        op[130] = getOpcodeRep((char)0, Character.MAX_VALUE, 131);
        {int[] a = {132,133}; op[131] = getOpcodeCat(a);}
        {char[] a = {47}; op[132] = getOpcodeTls(a);}
        op[133] = getOpcodeRnm(35, 149); // xri-segment
        {int[] a = {135,136}; op[134] = getOpcodeCat(a);}
        {char[] a = {47}; op[135] = getOpcodeTls(a);}
        op[136] = getOpcodeRep((char)0, (char)1, 137);
        {int[] a = {138,139}; op[137] = getOpcodeCat(a);}
        op[138] = getOpcodeRnm(36, 154); // xri-segment-nz
        op[139] = getOpcodeRep((char)0, Character.MAX_VALUE, 140);
        {int[] a = {141,142}; op[140] = getOpcodeCat(a);}
        {char[] a = {47}; op[141] = getOpcodeTls(a);}
        op[142] = getOpcodeRnm(35, 149); // xri-segment
        {int[] a = {144,145}; op[143] = getOpcodeCat(a);}
        op[144] = getOpcodeRnm(37, 160); // xri-segment-nc
        op[145] = getOpcodeRep((char)0, Character.MAX_VALUE, 146);
        {int[] a = {147,148}; op[146] = getOpcodeCat(a);}
        {char[] a = {47}; op[147] = getOpcodeTls(a);}
        op[148] = getOpcodeRnm(35, 149); // xri-segment
        {int[] a = {150,152}; op[149] = getOpcodeCat(a);}
        op[150] = getOpcodeRep((char)0, (char)1, 151);
        op[151] = getOpcodeRnm(25, 108); // literal
        op[152] = getOpcodeRep((char)0, Character.MAX_VALUE, 153);
        op[153] = getOpcodeRnm(20, 83); // subseg
        {int[] a = {155,158}; op[154] = getOpcodeCat(a);}
        {int[] a = {156,157}; op[155] = getOpcodeAlt(a);}
        op[156] = getOpcodeRnm(25, 108); // literal
        op[157] = getOpcodeRnm(20, 83); // subseg
        op[158] = getOpcodeRep((char)0, Character.MAX_VALUE, 159);
        op[159] = getOpcodeRnm(20, 83); // subseg
        {int[] a = {161,164}; op[160] = getOpcodeCat(a);}
        {int[] a = {162,163}; op[161] = getOpcodeAlt(a);}
        op[162] = getOpcodeRnm(26, 110); // literal-nc
        op[163] = getOpcodeRnm(20, 83); // subseg
        op[164] = getOpcodeRep((char)0, Character.MAX_VALUE, 165);
        op[165] = getOpcodeRnm(20, 83); // subseg
        {int[] a = {167,168,169,170}; op[166] = getOpcodeAlt(a);}
        op[167] = getOpcodeRnm(68, 432); // iunreserved
        op[168] = getOpcodeRnm(69, 440); // pct-encoded
        op[169] = getOpcodeRnm(42, 189); // xri-sub-delims
        {char[] a = {58}; op[170] = getOpcodeTls(a);}
        {int[] a = {172,173,174}; op[171] = getOpcodeAlt(a);}
        op[172] = getOpcodeRnm(68, 432); // iunreserved
        op[173] = getOpcodeRnm(69, 440); // pct-encoded
        op[174] = getOpcodeRnm(42, 189); // xri-sub-delims
        {int[] a = {176,177}; op[175] = getOpcodeAlt(a);}
        op[176] = getOpcodeRnm(41, 178); // xri-gen-delims
        op[177] = getOpcodeRnm(42, 189); // xri-sub-delims
        {int[] a = {179,180,181,182,183,184,185,186,187,188}; op[178] = getOpcodeAlt(a);}
        {char[] a = {58}; op[179] = getOpcodeTls(a);}
        {char[] a = {47}; op[180] = getOpcodeTls(a);}
        {char[] a = {63}; op[181] = getOpcodeTls(a);}
        {char[] a = {59}; op[182] = getOpcodeTls(a);}
        {char[] a = {91}; op[183] = getOpcodeTls(a);}
        {char[] a = {93}; op[184] = getOpcodeTls(a);}
        {char[] a = {40}; op[185] = getOpcodeTls(a);}
        {char[] a = {41}; op[186] = getOpcodeTls(a);}
        op[187] = getOpcodeRnm(23, 100); // gcs-char
        op[188] = getOpcodeRnm(24, 105); // lcs-char
        {int[] a = {190,191,192,193}; op[189] = getOpcodeAlt(a);}
        {char[] a = {38}; op[190] = getOpcodeTls(a);}
        {char[] a = {59}; op[191] = getOpcodeTls(a);}
        {char[] a = {44}; op[192] = getOpcodeTls(a);}
        {char[] a = {39}; op[193] = getOpcodeTls(a);}
        {int[] a = {195,196,197,198,202}; op[194] = getOpcodeCat(a);}
        op[195] = getOpcodeRnm(44, 206); // scheme
        {char[] a = {58}; op[196] = getOpcodeTls(a);}
        op[197] = getOpcodeRnm(45, 215); // ihier-part
        op[198] = getOpcodeRep((char)0, (char)1, 199);
        {int[] a = {200,201}; op[199] = getOpcodeCat(a);}
        {char[] a = {63}; op[200] = getOpcodeTls(a);}
        op[201] = getOpcodeRnm(64, 414); // iquery
        op[202] = getOpcodeRep((char)0, (char)1, 203);
        {int[] a = {204,205}; op[203] = getOpcodeCat(a);}
        {char[] a = {59}; op[204] = getOpcodeTls(a);}
        op[205] = getOpcodeRnm(66, 421); // ifragment
        {int[] a = {207,208}; op[206] = getOpcodeCat(a);}
        op[207] = getOpcodeRnm(75, 476); // ALPHA
        op[208] = getOpcodeRep((char)0, Character.MAX_VALUE, 209);
        {int[] a = {210,211,212,213,214}; op[209] = getOpcodeAlt(a);}
        op[210] = getOpcodeRnm(75, 476); // ALPHA
        op[211] = getOpcodeRnm(81, 490); // DIGIT
        {char[] a = {43}; op[212] = getOpcodeTls(a);}
        {char[] a = {45}; op[213] = getOpcodeTls(a);}
        {char[] a = {46}; op[214] = getOpcodeTls(a);}
        {int[] a = {216,220,221,222}; op[215] = getOpcodeAlt(a);}
        {int[] a = {217,218,219}; op[216] = getOpcodeCat(a);}
        {char[] a = {47,47}; op[217] = getOpcodeTls(a);}
        op[218] = getOpcodeRnm(46, 223); // iauthority
        op[219] = getOpcodeRnm(58, 390); // ipath-abempty
        op[220] = getOpcodeRnm(59, 394); // ipath-abs
        op[221] = getOpcodeRnm(60, 403); // ipath-rootless
        op[222] = getOpcodeRnm(61, 409); // ipath-empty
        {int[] a = {224,228,229}; op[223] = getOpcodeCat(a);}
        op[224] = getOpcodeRep((char)0, (char)1, 225);
        {int[] a = {226,227}; op[225] = getOpcodeCat(a);}
        op[226] = getOpcodeRnm(47, 233); // iuserinfo
        {char[] a = {64}; op[227] = getOpcodeTls(a);}
        op[228] = getOpcodeRnm(48, 239); // ihost
        op[229] = getOpcodeRep((char)0, (char)1, 230);
        {int[] a = {231,232}; op[230] = getOpcodeCat(a);}
        {char[] a = {58}; op[231] = getOpcodeTls(a);}
        op[232] = getOpcodeRnm(57, 388); // port
        op[233] = getOpcodeRep((char)0, Character.MAX_VALUE, 234);
        {int[] a = {235,236,237,238}; op[234] = getOpcodeAlt(a);}
        op[235] = getOpcodeRnm(68, 432); // iunreserved
        op[236] = getOpcodeRnm(69, 440); // pct-encoded
        op[237] = getOpcodeRnm(73, 459); // sub-delims
        {char[] a = {58}; op[238] = getOpcodeTls(a);}
        {int[] a = {240,241,242}; op[239] = getOpcodeAlt(a);}
        op[240] = getOpcodeRnm(49, 243); // IP-literal
        op[241] = getOpcodeRnm(54, 359); // IPv4address
        op[242] = getOpcodeRnm(56, 383); // ireg-name
        {int[] a = {244,245,248}; op[243] = getOpcodeCat(a);}
        {char[] a = {91}; op[244] = getOpcodeTls(a);}
        {int[] a = {246,247}; op[245] = getOpcodeAlt(a);}
        op[246] = getOpcodeRnm(51, 259); // IPv6address
        op[247] = getOpcodeRnm(50, 249); // IPvFuture
        {char[] a = {93}; op[248] = getOpcodeTls(a);}
        {int[] a = {250,251,253,254}; op[249] = getOpcodeCat(a);}
        {char[] a = {118}; op[250] = getOpcodeTls(a);}
        op[251] = getOpcodeRep((char)1, Character.MAX_VALUE, 252);
        op[252] = getOpcodeRnm(83, 492); // HEXDIG
        {char[] a = {46}; op[253] = getOpcodeTls(a);}
        op[254] = getOpcodeRep((char)1, Character.MAX_VALUE, 255);
        {int[] a = {256,257,258}; op[255] = getOpcodeAlt(a);}
        op[256] = getOpcodeRnm(74, 469); // unreserved
        op[257] = getOpcodeRnm(73, 459); // sub-delims
        {char[] a = {58}; op[258] = getOpcodeTls(a);}
        {int[] a = {260,266,273,282,296,310,322,332,342}; op[259] = getOpcodeAlt(a);}
        {int[] a = {261,265}; op[260] = getOpcodeCat(a);}
        op[261] = getOpcodeRep((char)6, (char)6, 262);
        {int[] a = {263,264}; op[262] = getOpcodeCat(a);}
        op[263] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[264] = getOpcodeTls(a);}
        op[265] = getOpcodeRnm(52, 351); // ls32
        {int[] a = {267,268,272}; op[266] = getOpcodeCat(a);}
        {char[] a = {58,58}; op[267] = getOpcodeTls(a);}
        op[268] = getOpcodeRep((char)5, (char)5, 269);
        {int[] a = {270,271}; op[269] = getOpcodeCat(a);}
        op[270] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[271] = getOpcodeTls(a);}
        op[272] = getOpcodeRnm(52, 351); // ls32
        {int[] a = {274,276,277,281}; op[273] = getOpcodeCat(a);}
        op[274] = getOpcodeRep((char)0, (char)1, 275);
        op[275] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58,58}; op[276] = getOpcodeTls(a);}
        op[277] = getOpcodeRep((char)4, (char)4, 278);
        {int[] a = {279,280}; op[278] = getOpcodeCat(a);}
        op[279] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[280] = getOpcodeTls(a);}
        op[281] = getOpcodeRnm(52, 351); // ls32
        {int[] a = {283,290,291,295}; op[282] = getOpcodeCat(a);}
        op[283] = getOpcodeRep((char)0, (char)1, 284);
        {int[] a = {285,289}; op[284] = getOpcodeCat(a);}
        op[285] = getOpcodeRep((char)0, (char)1, 286);
        {int[] a = {287,288}; op[286] = getOpcodeCat(a);}
        op[287] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[288] = getOpcodeTls(a);}
        op[289] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58,58}; op[290] = getOpcodeTls(a);}
        op[291] = getOpcodeRep((char)3, (char)3, 292);
        {int[] a = {293,294}; op[292] = getOpcodeCat(a);}
        op[293] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[294] = getOpcodeTls(a);}
        op[295] = getOpcodeRnm(52, 351); // ls32
        {int[] a = {297,304,305,309}; op[296] = getOpcodeCat(a);}
        op[297] = getOpcodeRep((char)0, (char)1, 298);
        {int[] a = {299,303}; op[298] = getOpcodeCat(a);}
        op[299] = getOpcodeRep((char)0, (char)2, 300);
        {int[] a = {301,302}; op[300] = getOpcodeCat(a);}
        op[301] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[302] = getOpcodeTls(a);}
        op[303] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58,58}; op[304] = getOpcodeTls(a);}
        op[305] = getOpcodeRep((char)2, (char)2, 306);
        {int[] a = {307,308}; op[306] = getOpcodeCat(a);}
        op[307] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[308] = getOpcodeTls(a);}
        op[309] = getOpcodeRnm(52, 351); // ls32
        {int[] a = {311,318,319,320,321}; op[310] = getOpcodeCat(a);}
        op[311] = getOpcodeRep((char)0, (char)1, 312);
        {int[] a = {313,317}; op[312] = getOpcodeCat(a);}
        op[313] = getOpcodeRep((char)0, (char)3, 314);
        {int[] a = {315,316}; op[314] = getOpcodeCat(a);}
        op[315] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[316] = getOpcodeTls(a);}
        op[317] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58,58}; op[318] = getOpcodeTls(a);}
        op[319] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[320] = getOpcodeTls(a);}
        op[321] = getOpcodeRnm(52, 351); // ls32
        {int[] a = {323,330,331}; op[322] = getOpcodeCat(a);}
        op[323] = getOpcodeRep((char)0, (char)1, 324);
        {int[] a = {325,329}; op[324] = getOpcodeCat(a);}
        op[325] = getOpcodeRep((char)0, (char)4, 326);
        {int[] a = {327,328}; op[326] = getOpcodeCat(a);}
        op[327] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[328] = getOpcodeTls(a);}
        op[329] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58,58}; op[330] = getOpcodeTls(a);}
        op[331] = getOpcodeRnm(52, 351); // ls32
        {int[] a = {333,340,341}; op[332] = getOpcodeCat(a);}
        op[333] = getOpcodeRep((char)0, (char)1, 334);
        {int[] a = {335,339}; op[334] = getOpcodeCat(a);}
        op[335] = getOpcodeRep((char)0, (char)5, 336);
        {int[] a = {337,338}; op[336] = getOpcodeCat(a);}
        op[337] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[338] = getOpcodeTls(a);}
        op[339] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58,58}; op[340] = getOpcodeTls(a);}
        op[341] = getOpcodeRnm(53, 357); // h16
        {int[] a = {343,350}; op[342] = getOpcodeCat(a);}
        op[343] = getOpcodeRep((char)0, (char)1, 344);
        {int[] a = {345,349}; op[344] = getOpcodeCat(a);}
        op[345] = getOpcodeRep((char)0, (char)6, 346);
        {int[] a = {347,348}; op[346] = getOpcodeCat(a);}
        op[347] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[348] = getOpcodeTls(a);}
        op[349] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58,58}; op[350] = getOpcodeTls(a);}
        {int[] a = {352,356}; op[351] = getOpcodeAlt(a);}
        {int[] a = {353,354,355}; op[352] = getOpcodeCat(a);}
        op[353] = getOpcodeRnm(53, 357); // h16
        {char[] a = {58}; op[354] = getOpcodeTls(a);}
        op[355] = getOpcodeRnm(53, 357); // h16
        op[356] = getOpcodeRnm(54, 359); // IPv4address
        op[357] = getOpcodeRep((char)1, (char)4, 358);
        op[358] = getOpcodeRnm(83, 492); // HEXDIG
        {int[] a = {360,361,362,363,364,365,366}; op[359] = getOpcodeCat(a);}
        op[360] = getOpcodeRnm(55, 367); // dec-octet
        {char[] a = {46}; op[361] = getOpcodeTls(a);}
        op[362] = getOpcodeRnm(55, 367); // dec-octet
        {char[] a = {46}; op[363] = getOpcodeTls(a);}
        op[364] = getOpcodeRnm(55, 367); // dec-octet
        {char[] a = {46}; op[365] = getOpcodeTls(a);}
        op[366] = getOpcodeRnm(55, 367); // dec-octet
        {int[] a = {368,369,372,376,380}; op[367] = getOpcodeAlt(a);}
        op[368] = getOpcodeRnm(81, 490); // DIGIT
        {int[] a = {370,371}; op[369] = getOpcodeCat(a);}
        op[370] = getOpcodeTrg((char)49, (char)57);
        op[371] = getOpcodeRnm(81, 490); // DIGIT
        {int[] a = {373,374}; op[372] = getOpcodeCat(a);}
        {char[] a = {49}; op[373] = getOpcodeTls(a);}
        op[374] = getOpcodeRep((char)2, (char)2, 375);
        op[375] = getOpcodeRnm(81, 490); // DIGIT
        {int[] a = {377,378,379}; op[376] = getOpcodeCat(a);}
        {char[] a = {50}; op[377] = getOpcodeTls(a);}
        op[378] = getOpcodeTrg((char)48, (char)52);
        op[379] = getOpcodeRnm(81, 490); // DIGIT
        {int[] a = {381,382}; op[380] = getOpcodeCat(a);}
        {char[] a = {50,53}; op[381] = getOpcodeTls(a);}
        op[382] = getOpcodeTrg((char)48, (char)53);
        op[383] = getOpcodeRep((char)0, Character.MAX_VALUE, 384);
        {int[] a = {385,386,387}; op[384] = getOpcodeAlt(a);}
        op[385] = getOpcodeRnm(68, 432); // iunreserved
        op[386] = getOpcodeRnm(69, 440); // pct-encoded
        op[387] = getOpcodeRnm(73, 459); // sub-delims
        op[388] = getOpcodeRep((char)0, Character.MAX_VALUE, 389);
        op[389] = getOpcodeRnm(81, 490); // DIGIT
        op[390] = getOpcodeRep((char)0, Character.MAX_VALUE, 391);
        {int[] a = {392,393}; op[391] = getOpcodeCat(a);}
        {char[] a = {47}; op[392] = getOpcodeTls(a);}
        op[393] = getOpcodeRnm(62, 410); // isegment
        {int[] a = {395,396}; op[394] = getOpcodeCat(a);}
        {char[] a = {47}; op[395] = getOpcodeTls(a);}
        op[396] = getOpcodeRep((char)0, (char)1, 397);
        {int[] a = {398,399}; op[397] = getOpcodeCat(a);}
        op[398] = getOpcodeRnm(63, 412); // isegment-nz
        op[399] = getOpcodeRep((char)0, Character.MAX_VALUE, 400);
        {int[] a = {401,402}; op[400] = getOpcodeCat(a);}
        {char[] a = {47}; op[401] = getOpcodeTls(a);}
        op[402] = getOpcodeRnm(62, 410); // isegment
        {int[] a = {404,405}; op[403] = getOpcodeCat(a);}
        op[404] = getOpcodeRnm(63, 412); // isegment-nz
        op[405] = getOpcodeRep((char)0, Character.MAX_VALUE, 406);
        {int[] a = {407,408}; op[406] = getOpcodeCat(a);}
        {char[] a = {47}; op[407] = getOpcodeTls(a);}
        op[408] = getOpcodeRnm(62, 410); // isegment
        {char[] a = {}; op[409] = getOpcodeTls(a);}
        op[410] = getOpcodeRep((char)0, Character.MAX_VALUE, 411);
        op[411] = getOpcodeRnm(67, 426); // ipchar
        op[412] = getOpcodeRep((char)1, Character.MAX_VALUE, 413);
        op[413] = getOpcodeRnm(67, 426); // ipchar
        op[414] = getOpcodeRep((char)0, Character.MAX_VALUE, 415);
        {int[] a = {416,417,418,419}; op[415] = getOpcodeAlt(a);}
        op[416] = getOpcodeRnm(67, 426); // ipchar
        op[417] = getOpcodeRnm(65, 420); // iprivate
        {char[] a = {47}; op[418] = getOpcodeTls(a);}
        {char[] a = {63}; op[419] = getOpcodeTls(a);}
        op[420] = getOpcodeTrg((char)57344, (char)63743);
        op[421] = getOpcodeRep((char)0, Character.MAX_VALUE, 422);
        {int[] a = {423,424,425}; op[422] = getOpcodeAlt(a);}
        op[423] = getOpcodeRnm(67, 426); // ipchar
        {char[] a = {47}; op[424] = getOpcodeTls(a);}
        {char[] a = {63}; op[425] = getOpcodeTls(a);}
        {int[] a = {427,428,429,430,431}; op[426] = getOpcodeAlt(a);}
        op[427] = getOpcodeRnm(68, 432); // iunreserved
        op[428] = getOpcodeRnm(69, 440); // pct-encoded
        op[429] = getOpcodeRnm(73, 459); // sub-delims
        {char[] a = {58}; op[430] = getOpcodeTls(a);}
        {char[] a = {64}; op[431] = getOpcodeTls(a);}
        {int[] a = {433,434,435,436,437,438,439}; op[432] = getOpcodeAlt(a);}
        op[433] = getOpcodeRnm(75, 476); // ALPHA
        op[434] = getOpcodeRnm(81, 490); // DIGIT
        {char[] a = {45}; op[435] = getOpcodeTls(a);}
        {char[] a = {46}; op[436] = getOpcodeTls(a);}
        {char[] a = {95}; op[437] = getOpcodeTls(a);}
        {char[] a = {126}; op[438] = getOpcodeTls(a);}
        op[439] = getOpcodeRnm(70, 444); // ucschar
        {int[] a = {441,442,443}; op[440] = getOpcodeCat(a);}
        {char[] a = {37}; op[441] = getOpcodeTls(a);}
        op[442] = getOpcodeRnm(83, 492); // HEXDIG
        op[443] = getOpcodeRnm(83, 492); // HEXDIG
        {int[] a = {445,446,447}; op[444] = getOpcodeAlt(a);}
        op[445] = getOpcodeTrg((char)160, (char)55295);
        op[446] = getOpcodeTrg((char)63744, (char)64975);
        op[447] = getOpcodeTrg((char)65008, (char)65519);
        {int[] a = {449,450}; op[448] = getOpcodeAlt(a);}
        op[449] = getOpcodeRnm(72, 451); // gen-delims
        op[450] = getOpcodeRnm(73, 459); // sub-delims
        {int[] a = {452,453,454,455,456,457,458}; op[451] = getOpcodeAlt(a);}
        {char[] a = {58}; op[452] = getOpcodeTls(a);}
        {char[] a = {47}; op[453] = getOpcodeTls(a);}
        {char[] a = {63}; op[454] = getOpcodeTls(a);}
        {char[] a = {59}; op[455] = getOpcodeTls(a);}
        {char[] a = {91}; op[456] = getOpcodeTls(a);}
        {char[] a = {93}; op[457] = getOpcodeTls(a);}
        {char[] a = {64}; op[458] = getOpcodeTls(a);}
        {int[] a = {460,461,462,463,464,465,466,467,468}; op[459] = getOpcodeAlt(a);}
        {char[] a = {33}; op[460] = getOpcodeTls(a);}
        {char[] a = {36}; op[461] = getOpcodeTls(a);}
        {char[] a = {38}; op[462] = getOpcodeTls(a);}
        {char[] a = {39}; op[463] = getOpcodeTls(a);}
        {char[] a = {42}; op[464] = getOpcodeTls(a);}
        {char[] a = {43}; op[465] = getOpcodeTls(a);}
        {char[] a = {44}; op[466] = getOpcodeTls(a);}
        {char[] a = {59}; op[467] = getOpcodeTls(a);}
        {char[] a = {61}; op[468] = getOpcodeTls(a);}
        {int[] a = {470,471,472,473,474,475}; op[469] = getOpcodeAlt(a);}
        op[470] = getOpcodeRnm(75, 476); // ALPHA
        op[471] = getOpcodeRnm(81, 490); // DIGIT
        {char[] a = {45}; op[472] = getOpcodeTls(a);}
        {char[] a = {46}; op[473] = getOpcodeTls(a);}
        {char[] a = {95}; op[474] = getOpcodeTls(a);}
        {char[] a = {126}; op[475] = getOpcodeTls(a);}
        {int[] a = {477,478}; op[476] = getOpcodeAlt(a);}
        op[477] = getOpcodeTrg((char)65, (char)90);
        op[478] = getOpcodeTrg((char)97, (char)122);
        {int[] a = {480,481}; op[479] = getOpcodeAlt(a);}
        {char[] a = {48}; op[480] = getOpcodeTls(a);}
        {char[] a = {49}; op[481] = getOpcodeTls(a);}
        op[482] = getOpcodeTrg((char)1, (char)127);
        {char[] a = {13}; op[483] = getOpcodeTbs(a);}
        {int[] a = {485,486}; op[484] = getOpcodeCat(a);}
        op[485] = getOpcodeRnm(78, 483); // CR
        op[486] = getOpcodeRnm(85, 501); // LF
        {int[] a = {488,489}; op[487] = getOpcodeAlt(a);}
        op[488] = getOpcodeTrg((char)0, (char)31);
        {char[] a = {127}; op[489] = getOpcodeTbs(a);}
        op[490] = getOpcodeTrg((char)48, (char)57);
        {char[] a = {34}; op[491] = getOpcodeTbs(a);}
        {int[] a = {493,494,495,496,497,498,499}; op[492] = getOpcodeAlt(a);}
        op[493] = getOpcodeRnm(81, 490); // DIGIT
        {char[] a = {65}; op[494] = getOpcodeTls(a);}
        {char[] a = {66}; op[495] = getOpcodeTls(a);}
        {char[] a = {67}; op[496] = getOpcodeTls(a);}
        {char[] a = {68}; op[497] = getOpcodeTls(a);}
        {char[] a = {69}; op[498] = getOpcodeTls(a);}
        {char[] a = {70}; op[499] = getOpcodeTls(a);}
        {char[] a = {9}; op[500] = getOpcodeTbs(a);}
        {char[] a = {10}; op[501] = getOpcodeTbs(a);}
        op[502] = getOpcodeRep((char)0, Character.MAX_VALUE, 503);
        {int[] a = {504,505}; op[503] = getOpcodeAlt(a);}
        op[504] = getOpcodeRnm(90, 511); // WSP
        {int[] a = {506,507}; op[505] = getOpcodeCat(a);}
        op[506] = getOpcodeRnm(79, 484); // CRLF
        op[507] = getOpcodeRnm(90, 511); // WSP
        op[508] = getOpcodeTrg((char)0, (char)255);
        {char[] a = {32}; op[509] = getOpcodeTbs(a);}
        op[510] = getOpcodeTrg((char)33, (char)126);
        {int[] a = {512,513}; op[511] = getOpcodeAlt(a);}
        op[512] = getOpcodeRnm(88, 509); // SP
        op[513] = getOpcodeRnm(84, 500); // HTAB
        return op;
    }

    public static void display(PrintStream out){
        out.println(";");
        out.println("; xdi2.core.xri3.impl.parser.aparse.XDI");
        out.println(";");
        out.println(";");
        out.println("; This file contains ABNF rules from the following sources:");
        out.println("; http://wiki.oasis-open.org/xri/XriThree/SyntaxAbnf");
        out.println("; http://wiki.oasis-open.org/xdi/XdiAbnf");
        out.println(";");
        out.println("; A few modifications have been made to the rules in order to be compatible with ");
        out.println("; Java and the APG library:");
        out.println(";");
        out.println("; 1) APG doesn't support prose-val. the \"ipath-empty\" rule has been adjusted accordingly.");
        out.println("; 2) Java regular expressions only support the BMP of unicode. the \"ucschar\" and \"iprivate\"");
        out.println(";    rules have been adjusted accordingly.");
        out.println(";");
        out.println("; =markus 01-25-2013");
        out.println(";");
        out.println("");
        out.println(";");
        out.println("; XDI rules");
        out.println(";");
        out.println("");
        out.println("xdi-address = xdi-statement / xdi-context;");
        out.println("xdi-context = xdi-segment");
        out.println("xdi-statement = xdi-subject \"/\" xdi-predicate \"/\" xdi-object");
        out.println("xdi-subject = xdi-segment");
        out.println("xdi-predicate = xdi-segment");
        out.println("xdi-object = xdi-segment");
        out.println("xdi-segment = ( literal / xdi-subseg ) *xdi-subseg");
        out.println("xdi-subseg = xdi-global-subseg / xdi-local-subseg / xdi-xref");
        out.println("xdi-global-subseg = gcs-char [ xdi-local-subseg / xdi-xref / literal ]");
        out.println("xdi-local-subseg = lcs-char [ xdi-xref / literal ]");
        out.println("xdi-xref = xdi-xref-empty / xdi-xref-IRI / xdi-xref-address;");
        out.println("xdi-xref-empty = \"()\"");
        out.println("xdi-xref-IRI = \"(\" IRI \")\"");
        out.println("xdi-xref-address = \"(\" xdi-address \")\"");
        out.println("");
        out.println(";");
        out.println("; XRI rules");
        out.println(";");
        out.println("");
        out.println("xri               = xri-hier-part [ \"?\" iquery ] [ \";\" ifragment ]");
        out.println("xri-reference     = xri");
        out.println("                  / relative-xri-ref");
        out.println("relative-xri-ref  = relative-xri-part [ \"?\" iquery ] [ \";\" ifragment ]");
        out.println("relative-xri-part = xri-path-abs");
        out.println("                  / xri-path-noscheme");
        out.println("                  / ipath-empty");
        out.println("xri-hier-part     = xri-authority xri-path-abempty");
        out.println("xri-authority     = global-subseg *subseg");
        out.println("subseg            = global-subseg");
        out.println("                  / local-subseg");
        out.println("                  / xref");
        out.println("global-subseg     = gcs-char [ local-subseg / xref / literal ]");
        out.println("local-subseg      = lcs-char [ xref / literal ]");
        out.println("gcs-char          = \"=\" / \"@\" / \"+\" / \"$\"");
        out.println("lcs-char          = \"*\" / \"!\"");
        out.println("literal           = 1*xri-pchar");
        out.println("literal-nc        = 1*xri-pchar-nc");
        out.println("xref              = xref-empty / xref-xri-reference / xref-IRI;");
        out.println("xref-empty        = \"()\"");
        out.println("xref-xri-reference = \"(\" xri-reference \")\"");
        out.println("xref-IRI          = \"(\" IRI \")\"");
        out.println("xri-path          = xri-path-abempty");
        out.println("                  / xri-path-abs");
        out.println("                  / xri-path-noscheme");
        out.println("                  / ipath-empty");
        out.println("xri-path-abempty  = *( \"/\" xri-segment )");
        out.println("xri-path-abs      = \"/\" [ xri-segment-nz *( \"/\" xri-segment ) ]");
        out.println("xri-path-noscheme = xri-segment-nc *( \"/\" xri-segment )");
        out.println("xri-segment       = [ literal ] *subseg");
        out.println("xri-segment-nz    = ( literal / subseg ) *subseg");
        out.println("xri-segment-nc    = ( literal-nc / subseg ) *subseg");
        out.println("xri-pchar         = iunreserved / pct-encoded / xri-sub-delims / \":\"");
        out.println("xri-pchar-nc      = iunreserved / pct-encoded / xri-sub-delims");
        out.println("xri-reserved      = xri-gen-delims / xri-sub-delims");
        out.println("xri-gen-delims    = \":\" / \"/\" / \"?\" / \";\" / \"[\" / \"]\" / \"(\" / \")\"");
        out.println("                  / gcs-char / lcs-char");
        out.println("xri-sub-delims    = \"&\" / \";\" / \",\" / \"'\"");
        out.println("");
        out.println(";");
        out.println("; IRI rules");
        out.println(";");
        out.println("");
        out.println("IRI               = scheme \":\" ihier-part [ \"?\" iquery ]");
        out.println("                  [ \";\" ifragment ]");
        out.println("scheme            = ALPHA *( ALPHA / DIGIT / \"+\" / \"-\" / \".\" )");
        out.println("ihier-part        = \"//\" iauthority ipath-abempty");
        out.println("                  / ipath-abs");
        out.println("                  / ipath-rootless");
        out.println("                  / ipath-empty");
        out.println("iauthority        = [ iuserinfo \"@\" ] ihost [ \":\" port ]");
        out.println("iuserinfo         = *( iunreserved / pct-encoded / sub-delims / \":\" )");
        out.println("ihost             = IP-literal / IPv4address / ireg-name");
        out.println("IP-literal        = \"[\" ( IPv6address / IPvFuture  ) \"]\"");
        out.println("IPvFuture         = \"v\" 1*HEXDIG \".\" 1*( unreserved / sub-delims / \":\" )");
        out.println("IPv6address       =                            6( h16 \":\" ) ls32");
        out.println("                  /                       \"::\" 5( h16 \":\" ) ls32");
        out.println("                  / [               h16 ] \"::\" 4( h16 \":\" ) ls32");
        out.println("                  / [ *1( h16 \":\" ) h16 ] \"::\" 3( h16 \":\" ) ls32");
        out.println("                  / [ *2( h16 \":\" ) h16 ] \"::\" 2( h16 \":\" ) ls32");
        out.println("                  / [ *3( h16 \":\" ) h16 ] \"::\"    h16 \":\"   ls32");
        out.println("                  / [ *4( h16 \":\" ) h16 ] \"::\"              ls32");
        out.println("                  / [ *5( h16 \":\" ) h16 ] \"::\"              h16");
        out.println("                  / [ *6( h16 \":\" ) h16 ] \"::\"");
        out.println("ls32              = ( h16 \":\" h16 ) / IPv4address");
        out.println("h16               = 1*4HEXDIG");
        out.println("IPv4address       = dec-octet \".\" dec-octet \".\" dec-octet \".\" dec-octet");
        out.println("dec-octet         = DIGIT                ; 0-9");
        out.println("                  / %x31-39 DIGIT        ; 10-99");
        out.println("                  / \"1\" 2DIGIT           ; 100-199");
        out.println("                  / \"2\" %x30-34 DIGIT    ; 200-249");
        out.println("                  / \"25\" %x30-35       ; 250-255");
        out.println("ireg-name         = *( iunreserved / pct-encoded / sub-delims )");
        out.println("port              = *DIGIT");
        out.println("ipath-abempty     = *( \"/\" isegment )");
        out.println("ipath-abs         = \"/\" [ isegment-nz *( \"/\" isegment ) ]");
        out.println("ipath-rootless    = isegment-nz *( \"/\" isegment )");
        out.println("ipath-empty       = \"\"");
        out.println("isegment          = *ipchar");
        out.println("isegment-nz       = 1*ipchar");
        out.println("iquery            = *( ipchar / iprivate / \"/\" / \"?\" )");
        out.println("iprivate          = %xE000-F8FF");
        out.println("ifragment         = *( ipchar / \"/\" / \"?\" )");
        out.println("ipchar            = iunreserved / pct-encoded / sub-delims / \":\" / \"@\"");
        out.println("iunreserved       = ALPHA / DIGIT / \"-\" / \".\" / \"_\" / \"~\" / ucschar");
        out.println("pct-encoded       = \"%\" HEXDIG HEXDIG");
        out.println("ucschar           = %xA0-D7FF / %xF900-FDCF / %xFDF0-FFEF");
        out.println("reserved          = gen-delims / sub-delims");
        out.println("gen-delims        = \":\" / \"/\" / \"?\" / \";\" / \"[\" / \"]\" / \"@\"");
        out.println("sub-delims        = \"!\" / \"$\" / \"&\" / \"'\" ");
        out.println("                  / \"*\" / \"+\" / \",\" / \";\" / \"=\"");
        out.println("unreserved        = ALPHA / DIGIT / \"-\" / \".\" / \"_\" / \"~\"");
        out.println("");
        out.println(";");
        out.println("; ABNF core rules");
        out.println(";");
        out.println("");
        out.println("ALPHA          =  %x41-5A / %x61-7A 		; A-Z / a-z");
        out.println("BIT            =  \"0\" / \"1\"");
        out.println("CHAR           =  %x01-7F 				; any 7-bit US-ASCII character, excluding NUL");
        out.println("CR             =  %x0D					; carriage return");
        out.println("CRLF           =  CR LF					; Internet standard newline");
        out.println("CTL            =  %x00-1F / %x7F			; controls");
        out.println("DIGIT          =  %x30-39 				; 0-9");
        out.println("DQUOTE         =  %x22					; \" (Double Quote)");
        out.println("HEXDIG         =  DIGIT / \"A\" / \"B\" / \"C\" / \"D\" / \"E\" / \"F\"");
        out.println("HTAB           =  %x09					; horizontal tab");
        out.println("LF             =  %x0A					; linefeed");
        out.println("LWSP           =  *(WSP / CRLF WSP)		; linear white space (past newline)");
        out.println("OCTET          =  %x00-FF					; 8 bits of data");
        out.println("SP             =  %x20					; space");
        out.println("VCHAR          =  %x21-7E					; visible (printing) characters");
        out.println("WSP            =  SP / HTAB				; white space");
    }
}
