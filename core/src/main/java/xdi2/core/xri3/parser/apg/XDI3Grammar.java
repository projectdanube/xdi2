package xdi2.core.xri3.parser.apg;

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
    public static int ruleCount = 72;
    public enum RuleNames{
        ALPHA("ALPHA", 56, 382, 3),
        BIT("BIT", 57, 385, 3),
        CHAR("CHAR", 58, 388, 1),
        CR("CR", 59, 389, 1),
        CRLF("CRLF", 60, 390, 3),
        CTL("CTL", 61, 393, 3),
        DEC_OCTET("dec-octet", 36, 273, 16),
        DIGIT("DIGIT", 62, 396, 1),
        DQUOTE("DQUOTE", 63, 397, 1),
        GCS_CHAR("gcs-char", 10, 39, 5),
        GEN_DELIMS("gen-delims", 53, 357, 8),
        GLOBAL_SUBSEG("global-subseg", 8, 26, 7),
        H16("h16", 34, 263, 2),
        HEXDIG("HEXDIG", 64, 398, 8),
        HTAB("HTAB", 65, 406, 1),
        IAUTHORITY("iauthority", 27, 129, 10),
        IFRAGMENT("ifragment", 47, 327, 5),
        IHIER_PART("ihier-part", 26, 121, 8),
        IHOST("ihost", 29, 145, 4),
        IP_LITERAL("IP-literal", 30, 149, 6),
        IPATH_ABEMPTY("ipath-abempty", 39, 296, 4),
        IPATH_ABS("ipath-abs", 40, 300, 9),
        IPATH_EMPTY("ipath-empty", 42, 315, 1),
        IPATH_ROOTLESS("ipath-rootless", 41, 309, 6),
        IPCHAR("ipchar", 48, 332, 6),
        IPRIVATE("iprivate", 46, 326, 1),
        IPV4ADDRESS("IPv4address", 35, 265, 8),
        IPV6ADDRESS("IPv6address", 32, 165, 92),
        IPVFUTURE("IPvFuture", 31, 155, 10),
        IQUERY("iquery", 45, 320, 6),
        IREG_NAME("ireg-name", 37, 289, 5),
        IRI("IRI", 24, 100, 12),
        ISEGMENT("isegment", 43, 316, 2),
        ISEGMENT_NZ("isegment-nz", 44, 318, 2),
        IUNRESERVED("iunreserved", 49, 338, 8),
        IUSERINFO("iuserinfo", 28, 139, 6),
        LCS_CHAR("lcs-char", 11, 44, 3),
        LF("LF", 66, 407, 1),
        LITERAL("literal", 18, 70, 2),
        LOCAL_SUBSEG("local-subseg", 9, 33, 6),
        LS32("ls32", 33, 257, 6),
        LWSP("LWSP", 67, 408, 6),
        OCTET("OCTET", 68, 414, 1),
        PCT_ENCODED("pct-encoded", 50, 346, 4),
        PORT("port", 38, 294, 2),
        RESERVED("reserved", 52, 354, 3),
        SCHEME("scheme", 25, 112, 9),
        SP("SP", 69, 415, 1),
        SUB_DELIMS("sub-delims", 54, 365, 10),
        SUBSEG("subseg", 7, 22, 4),
        UCSCHAR("ucschar", 51, 350, 4),
        UNRESERVED("unreserved", 55, 375, 7),
        VCHAR("VCHAR", 70, 416, 1),
        WSP("WSP", 71, 417, 3),
        XDI_ADDRESS("xdi-address", 0, 0, 3),
        XDI_GEN_DELIMS("xdi-gen-delims", 22, 84, 11),
        XDI_INNER("xdi-inner", 2, 9, 4),
        XDI_OBJECT("xdi-object", 5, 15, 1),
        XDI_PCHAR("xdi-pchar", 19, 72, 5),
        XDI_PCHAR_NC("xdi-pchar-nc", 20, 77, 4),
        XDI_PREDICATE("xdi-predicate", 4, 14, 1),
        XDI_RESERVED("xdi-reserved", 21, 81, 3),
        XDI_SEGMENT("xdi-segment", 6, 16, 6),
        XDI_STATEMENT("xdi-statement", 1, 3, 6),
        XDI_SUB_DELIMS("xdi-sub-delims", 23, 95, 5),
        XDI_SUBJECT("xdi-subject", 3, 13, 1),
        XREF("xref", 12, 47, 6),
        XREF_EMPTY("xref-empty", 13, 53, 1),
        XREF_INNER("xref-inner", 16, 62, 4),
        XREF_IRI("xref-IRI", 14, 54, 4),
        XREF_STATEMENT("xref-statement", 17, 66, 4),
        XREF_SUBJECT("xref-subject", 15, 58, 4);
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
    	Rule[] rules = new Rule[72];
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
    	Opcode[] op = new Opcode[420];
        {int[] a = {1,2}; op[0] = getOpcodeAlt(a);}
        op[1] = getOpcodeRnm(3, 13); // xdi-subject
        op[2] = getOpcodeRnm(1, 3); // xdi-statement
        {int[] a = {4,5,6,7,8}; op[3] = getOpcodeCat(a);}
        op[4] = getOpcodeRnm(3, 13); // xdi-subject
        {char[] a = {47}; op[5] = getOpcodeTls(a);}
        op[6] = getOpcodeRnm(4, 14); // xdi-predicate
        {char[] a = {47}; op[7] = getOpcodeTls(a);}
        op[8] = getOpcodeRnm(5, 15); // xdi-object
        {int[] a = {10,11,12}; op[9] = getOpcodeCat(a);}
        op[10] = getOpcodeRnm(3, 13); // xdi-subject
        {char[] a = {47}; op[11] = getOpcodeTls(a);}
        op[12] = getOpcodeRnm(4, 14); // xdi-predicate
        op[13] = getOpcodeRnm(6, 16); // xdi-segment
        op[14] = getOpcodeRnm(6, 16); // xdi-segment
        op[15] = getOpcodeRnm(6, 16); // xdi-segment
        {int[] a = {17,20}; op[16] = getOpcodeCat(a);}
        {int[] a = {18,19}; op[17] = getOpcodeAlt(a);}
        op[18] = getOpcodeRnm(18, 70); // literal
        op[19] = getOpcodeRnm(7, 22); // subseg
        op[20] = getOpcodeRep((char)0, Character.MAX_VALUE, 21);
        op[21] = getOpcodeRnm(7, 22); // subseg
        {int[] a = {23,24,25}; op[22] = getOpcodeAlt(a);}
        op[23] = getOpcodeRnm(8, 26); // global-subseg
        op[24] = getOpcodeRnm(9, 33); // local-subseg
        op[25] = getOpcodeRnm(12, 47); // xref
        {int[] a = {27,28}; op[26] = getOpcodeCat(a);}
        op[27] = getOpcodeRnm(10, 39); // gcs-char
        op[28] = getOpcodeRep((char)0, (char)1, 29);
        {int[] a = {30,31,32}; op[29] = getOpcodeAlt(a);}
        op[30] = getOpcodeRnm(9, 33); // local-subseg
        op[31] = getOpcodeRnm(12, 47); // xref
        op[32] = getOpcodeRnm(18, 70); // literal
        {int[] a = {34,35}; op[33] = getOpcodeCat(a);}
        op[34] = getOpcodeRnm(11, 44); // lcs-char
        op[35] = getOpcodeRep((char)0, (char)1, 36);
        {int[] a = {37,38}; op[36] = getOpcodeAlt(a);}
        op[37] = getOpcodeRnm(12, 47); // xref
        op[38] = getOpcodeRnm(18, 70); // literal
        {int[] a = {40,41,42,43}; op[39] = getOpcodeAlt(a);}
        {char[] a = {61}; op[40] = getOpcodeTls(a);}
        {char[] a = {64}; op[41] = getOpcodeTls(a);}
        {char[] a = {43}; op[42] = getOpcodeTls(a);}
        {char[] a = {36}; op[43] = getOpcodeTls(a);}
        {int[] a = {45,46}; op[44] = getOpcodeAlt(a);}
        {char[] a = {42}; op[45] = getOpcodeTls(a);}
        {char[] a = {33}; op[46] = getOpcodeTls(a);}
        {int[] a = {48,49,50,51,52}; op[47] = getOpcodeAlt(a);}
        op[48] = getOpcodeRnm(13, 53); // xref-empty
        op[49] = getOpcodeRnm(14, 54); // xref-IRI
        op[50] = getOpcodeRnm(15, 58); // xref-subject
        op[51] = getOpcodeRnm(16, 62); // xref-inner
        op[52] = getOpcodeRnm(17, 66); // xref-statement
        {char[] a = {40,41}; op[53] = getOpcodeTls(a);}
        {int[] a = {55,56,57}; op[54] = getOpcodeCat(a);}
        {char[] a = {40}; op[55] = getOpcodeTls(a);}
        op[56] = getOpcodeRnm(24, 100); // IRI
        {char[] a = {41}; op[57] = getOpcodeTls(a);}
        {int[] a = {59,60,61}; op[58] = getOpcodeCat(a);}
        {char[] a = {40}; op[59] = getOpcodeTls(a);}
        op[60] = getOpcodeRnm(3, 13); // xdi-subject
        {char[] a = {41}; op[61] = getOpcodeTls(a);}
        {int[] a = {63,64,65}; op[62] = getOpcodeCat(a);}
        {char[] a = {40}; op[63] = getOpcodeTls(a);}
        op[64] = getOpcodeRnm(2, 9); // xdi-inner
        {char[] a = {41}; op[65] = getOpcodeTls(a);}
        {int[] a = {67,68,69}; op[66] = getOpcodeCat(a);}
        {char[] a = {40}; op[67] = getOpcodeTls(a);}
        op[68] = getOpcodeRnm(1, 3); // xdi-statement
        {char[] a = {41}; op[69] = getOpcodeTls(a);}
        op[70] = getOpcodeRep((char)1, Character.MAX_VALUE, 71);
        op[71] = getOpcodeRnm(19, 72); // xdi-pchar
        {int[] a = {73,74,75,76}; op[72] = getOpcodeAlt(a);}
        op[73] = getOpcodeRnm(49, 338); // iunreserved
        op[74] = getOpcodeRnm(50, 346); // pct-encoded
        op[75] = getOpcodeRnm(23, 95); // xdi-sub-delims
        {char[] a = {58}; op[76] = getOpcodeTls(a);}
        {int[] a = {78,79,80}; op[77] = getOpcodeAlt(a);}
        op[78] = getOpcodeRnm(49, 338); // iunreserved
        op[79] = getOpcodeRnm(50, 346); // pct-encoded
        op[80] = getOpcodeRnm(23, 95); // xdi-sub-delims
        {int[] a = {82,83}; op[81] = getOpcodeAlt(a);}
        op[82] = getOpcodeRnm(22, 84); // xdi-gen-delims
        op[83] = getOpcodeRnm(23, 95); // xdi-sub-delims
        {int[] a = {85,86,87,88,89,90,91,92,93,94}; op[84] = getOpcodeAlt(a);}
        {char[] a = {58}; op[85] = getOpcodeTls(a);}
        {char[] a = {47}; op[86] = getOpcodeTls(a);}
        {char[] a = {63}; op[87] = getOpcodeTls(a);}
        {char[] a = {35}; op[88] = getOpcodeTls(a);}
        {char[] a = {91}; op[89] = getOpcodeTls(a);}
        {char[] a = {93}; op[90] = getOpcodeTls(a);}
        {char[] a = {40}; op[91] = getOpcodeTls(a);}
        {char[] a = {41}; op[92] = getOpcodeTls(a);}
        op[93] = getOpcodeRnm(10, 39); // gcs-char
        op[94] = getOpcodeRnm(11, 44); // lcs-char
        {int[] a = {96,97,98,99}; op[95] = getOpcodeAlt(a);}
        {char[] a = {38}; op[96] = getOpcodeTls(a);}
        {char[] a = {59}; op[97] = getOpcodeTls(a);}
        {char[] a = {44}; op[98] = getOpcodeTls(a);}
        {char[] a = {39}; op[99] = getOpcodeTls(a);}
        {int[] a = {101,102,103,104,108}; op[100] = getOpcodeCat(a);}
        op[101] = getOpcodeRnm(25, 112); // scheme
        {char[] a = {58}; op[102] = getOpcodeTls(a);}
        op[103] = getOpcodeRnm(26, 121); // ihier-part
        op[104] = getOpcodeRep((char)0, (char)1, 105);
        {int[] a = {106,107}; op[105] = getOpcodeCat(a);}
        {char[] a = {63}; op[106] = getOpcodeTls(a);}
        op[107] = getOpcodeRnm(45, 320); // iquery
        op[108] = getOpcodeRep((char)0, (char)1, 109);
        {int[] a = {110,111}; op[109] = getOpcodeCat(a);}
        {char[] a = {35}; op[110] = getOpcodeTls(a);}
        op[111] = getOpcodeRnm(47, 327); // ifragment
        {int[] a = {113,114}; op[112] = getOpcodeCat(a);}
        op[113] = getOpcodeRnm(56, 382); // ALPHA
        op[114] = getOpcodeRep((char)0, Character.MAX_VALUE, 115);
        {int[] a = {116,117,118,119,120}; op[115] = getOpcodeAlt(a);}
        op[116] = getOpcodeRnm(56, 382); // ALPHA
        op[117] = getOpcodeRnm(62, 396); // DIGIT
        {char[] a = {43}; op[118] = getOpcodeTls(a);}
        {char[] a = {45}; op[119] = getOpcodeTls(a);}
        {char[] a = {46}; op[120] = getOpcodeTls(a);}
        {int[] a = {122,126,127,128}; op[121] = getOpcodeAlt(a);}
        {int[] a = {123,124,125}; op[122] = getOpcodeCat(a);}
        {char[] a = {47,47}; op[123] = getOpcodeTls(a);}
        op[124] = getOpcodeRnm(27, 129); // iauthority
        op[125] = getOpcodeRnm(39, 296); // ipath-abempty
        op[126] = getOpcodeRnm(40, 300); // ipath-abs
        op[127] = getOpcodeRnm(41, 309); // ipath-rootless
        op[128] = getOpcodeRnm(42, 315); // ipath-empty
        {int[] a = {130,134,135}; op[129] = getOpcodeCat(a);}
        op[130] = getOpcodeRep((char)0, (char)1, 131);
        {int[] a = {132,133}; op[131] = getOpcodeCat(a);}
        op[132] = getOpcodeRnm(28, 139); // iuserinfo
        {char[] a = {64}; op[133] = getOpcodeTls(a);}
        op[134] = getOpcodeRnm(29, 145); // ihost
        op[135] = getOpcodeRep((char)0, (char)1, 136);
        {int[] a = {137,138}; op[136] = getOpcodeCat(a);}
        {char[] a = {58}; op[137] = getOpcodeTls(a);}
        op[138] = getOpcodeRnm(38, 294); // port
        op[139] = getOpcodeRep((char)0, Character.MAX_VALUE, 140);
        {int[] a = {141,142,143,144}; op[140] = getOpcodeAlt(a);}
        op[141] = getOpcodeRnm(49, 338); // iunreserved
        op[142] = getOpcodeRnm(50, 346); // pct-encoded
        op[143] = getOpcodeRnm(54, 365); // sub-delims
        {char[] a = {58}; op[144] = getOpcodeTls(a);}
        {int[] a = {146,147,148}; op[145] = getOpcodeAlt(a);}
        op[146] = getOpcodeRnm(30, 149); // IP-literal
        op[147] = getOpcodeRnm(35, 265); // IPv4address
        op[148] = getOpcodeRnm(37, 289); // ireg-name
        {int[] a = {150,151,154}; op[149] = getOpcodeCat(a);}
        {char[] a = {91}; op[150] = getOpcodeTls(a);}
        {int[] a = {152,153}; op[151] = getOpcodeAlt(a);}
        op[152] = getOpcodeRnm(32, 165); // IPv6address
        op[153] = getOpcodeRnm(31, 155); // IPvFuture
        {char[] a = {93}; op[154] = getOpcodeTls(a);}
        {int[] a = {156,157,159,160}; op[155] = getOpcodeCat(a);}
        {char[] a = {118}; op[156] = getOpcodeTls(a);}
        op[157] = getOpcodeRep((char)1, Character.MAX_VALUE, 158);
        op[158] = getOpcodeRnm(64, 398); // HEXDIG
        {char[] a = {46}; op[159] = getOpcodeTls(a);}
        op[160] = getOpcodeRep((char)1, Character.MAX_VALUE, 161);
        {int[] a = {162,163,164}; op[161] = getOpcodeAlt(a);}
        op[162] = getOpcodeRnm(55, 375); // unreserved
        op[163] = getOpcodeRnm(54, 365); // sub-delims
        {char[] a = {58}; op[164] = getOpcodeTls(a);}
        {int[] a = {166,172,179,188,202,216,228,238,248}; op[165] = getOpcodeAlt(a);}
        {int[] a = {167,171}; op[166] = getOpcodeCat(a);}
        op[167] = getOpcodeRep((char)6, (char)6, 168);
        {int[] a = {169,170}; op[168] = getOpcodeCat(a);}
        op[169] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[170] = getOpcodeTls(a);}
        op[171] = getOpcodeRnm(33, 257); // ls32
        {int[] a = {173,174,178}; op[172] = getOpcodeCat(a);}
        {char[] a = {58,58}; op[173] = getOpcodeTls(a);}
        op[174] = getOpcodeRep((char)5, (char)5, 175);
        {int[] a = {176,177}; op[175] = getOpcodeCat(a);}
        op[176] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[177] = getOpcodeTls(a);}
        op[178] = getOpcodeRnm(33, 257); // ls32
        {int[] a = {180,182,183,187}; op[179] = getOpcodeCat(a);}
        op[180] = getOpcodeRep((char)0, (char)1, 181);
        op[181] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58,58}; op[182] = getOpcodeTls(a);}
        op[183] = getOpcodeRep((char)4, (char)4, 184);
        {int[] a = {185,186}; op[184] = getOpcodeCat(a);}
        op[185] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[186] = getOpcodeTls(a);}
        op[187] = getOpcodeRnm(33, 257); // ls32
        {int[] a = {189,196,197,201}; op[188] = getOpcodeCat(a);}
        op[189] = getOpcodeRep((char)0, (char)1, 190);
        {int[] a = {191,195}; op[190] = getOpcodeCat(a);}
        op[191] = getOpcodeRep((char)0, (char)1, 192);
        {int[] a = {193,194}; op[192] = getOpcodeCat(a);}
        op[193] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[194] = getOpcodeTls(a);}
        op[195] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58,58}; op[196] = getOpcodeTls(a);}
        op[197] = getOpcodeRep((char)3, (char)3, 198);
        {int[] a = {199,200}; op[198] = getOpcodeCat(a);}
        op[199] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[200] = getOpcodeTls(a);}
        op[201] = getOpcodeRnm(33, 257); // ls32
        {int[] a = {203,210,211,215}; op[202] = getOpcodeCat(a);}
        op[203] = getOpcodeRep((char)0, (char)1, 204);
        {int[] a = {205,209}; op[204] = getOpcodeCat(a);}
        op[205] = getOpcodeRep((char)0, (char)2, 206);
        {int[] a = {207,208}; op[206] = getOpcodeCat(a);}
        op[207] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[208] = getOpcodeTls(a);}
        op[209] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58,58}; op[210] = getOpcodeTls(a);}
        op[211] = getOpcodeRep((char)2, (char)2, 212);
        {int[] a = {213,214}; op[212] = getOpcodeCat(a);}
        op[213] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[214] = getOpcodeTls(a);}
        op[215] = getOpcodeRnm(33, 257); // ls32
        {int[] a = {217,224,225,226,227}; op[216] = getOpcodeCat(a);}
        op[217] = getOpcodeRep((char)0, (char)1, 218);
        {int[] a = {219,223}; op[218] = getOpcodeCat(a);}
        op[219] = getOpcodeRep((char)0, (char)3, 220);
        {int[] a = {221,222}; op[220] = getOpcodeCat(a);}
        op[221] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[222] = getOpcodeTls(a);}
        op[223] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58,58}; op[224] = getOpcodeTls(a);}
        op[225] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[226] = getOpcodeTls(a);}
        op[227] = getOpcodeRnm(33, 257); // ls32
        {int[] a = {229,236,237}; op[228] = getOpcodeCat(a);}
        op[229] = getOpcodeRep((char)0, (char)1, 230);
        {int[] a = {231,235}; op[230] = getOpcodeCat(a);}
        op[231] = getOpcodeRep((char)0, (char)4, 232);
        {int[] a = {233,234}; op[232] = getOpcodeCat(a);}
        op[233] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[234] = getOpcodeTls(a);}
        op[235] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58,58}; op[236] = getOpcodeTls(a);}
        op[237] = getOpcodeRnm(33, 257); // ls32
        {int[] a = {239,246,247}; op[238] = getOpcodeCat(a);}
        op[239] = getOpcodeRep((char)0, (char)1, 240);
        {int[] a = {241,245}; op[240] = getOpcodeCat(a);}
        op[241] = getOpcodeRep((char)0, (char)5, 242);
        {int[] a = {243,244}; op[242] = getOpcodeCat(a);}
        op[243] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[244] = getOpcodeTls(a);}
        op[245] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58,58}; op[246] = getOpcodeTls(a);}
        op[247] = getOpcodeRnm(34, 263); // h16
        {int[] a = {249,256}; op[248] = getOpcodeCat(a);}
        op[249] = getOpcodeRep((char)0, (char)1, 250);
        {int[] a = {251,255}; op[250] = getOpcodeCat(a);}
        op[251] = getOpcodeRep((char)0, (char)6, 252);
        {int[] a = {253,254}; op[252] = getOpcodeCat(a);}
        op[253] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[254] = getOpcodeTls(a);}
        op[255] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58,58}; op[256] = getOpcodeTls(a);}
        {int[] a = {258,262}; op[257] = getOpcodeAlt(a);}
        {int[] a = {259,260,261}; op[258] = getOpcodeCat(a);}
        op[259] = getOpcodeRnm(34, 263); // h16
        {char[] a = {58}; op[260] = getOpcodeTls(a);}
        op[261] = getOpcodeRnm(34, 263); // h16
        op[262] = getOpcodeRnm(35, 265); // IPv4address
        op[263] = getOpcodeRep((char)1, (char)4, 264);
        op[264] = getOpcodeRnm(64, 398); // HEXDIG
        {int[] a = {266,267,268,269,270,271,272}; op[265] = getOpcodeCat(a);}
        op[266] = getOpcodeRnm(36, 273); // dec-octet
        {char[] a = {46}; op[267] = getOpcodeTls(a);}
        op[268] = getOpcodeRnm(36, 273); // dec-octet
        {char[] a = {46}; op[269] = getOpcodeTls(a);}
        op[270] = getOpcodeRnm(36, 273); // dec-octet
        {char[] a = {46}; op[271] = getOpcodeTls(a);}
        op[272] = getOpcodeRnm(36, 273); // dec-octet
        {int[] a = {274,275,278,282,286}; op[273] = getOpcodeAlt(a);}
        op[274] = getOpcodeRnm(62, 396); // DIGIT
        {int[] a = {276,277}; op[275] = getOpcodeCat(a);}
        op[276] = getOpcodeTrg((char)49, (char)57);
        op[277] = getOpcodeRnm(62, 396); // DIGIT
        {int[] a = {279,280}; op[278] = getOpcodeCat(a);}
        {char[] a = {49}; op[279] = getOpcodeTls(a);}
        op[280] = getOpcodeRep((char)2, (char)2, 281);
        op[281] = getOpcodeRnm(62, 396); // DIGIT
        {int[] a = {283,284,285}; op[282] = getOpcodeCat(a);}
        {char[] a = {50}; op[283] = getOpcodeTls(a);}
        op[284] = getOpcodeTrg((char)48, (char)52);
        op[285] = getOpcodeRnm(62, 396); // DIGIT
        {int[] a = {287,288}; op[286] = getOpcodeCat(a);}
        {char[] a = {50,53}; op[287] = getOpcodeTls(a);}
        op[288] = getOpcodeTrg((char)48, (char)53);
        op[289] = getOpcodeRep((char)0, Character.MAX_VALUE, 290);
        {int[] a = {291,292,293}; op[290] = getOpcodeAlt(a);}
        op[291] = getOpcodeRnm(49, 338); // iunreserved
        op[292] = getOpcodeRnm(50, 346); // pct-encoded
        op[293] = getOpcodeRnm(54, 365); // sub-delims
        op[294] = getOpcodeRep((char)0, Character.MAX_VALUE, 295);
        op[295] = getOpcodeRnm(62, 396); // DIGIT
        op[296] = getOpcodeRep((char)0, Character.MAX_VALUE, 297);
        {int[] a = {298,299}; op[297] = getOpcodeCat(a);}
        {char[] a = {47}; op[298] = getOpcodeTls(a);}
        op[299] = getOpcodeRnm(43, 316); // isegment
        {int[] a = {301,302}; op[300] = getOpcodeCat(a);}
        {char[] a = {47}; op[301] = getOpcodeTls(a);}
        op[302] = getOpcodeRep((char)0, (char)1, 303);
        {int[] a = {304,305}; op[303] = getOpcodeCat(a);}
        op[304] = getOpcodeRnm(44, 318); // isegment-nz
        op[305] = getOpcodeRep((char)0, Character.MAX_VALUE, 306);
        {int[] a = {307,308}; op[306] = getOpcodeCat(a);}
        {char[] a = {47}; op[307] = getOpcodeTls(a);}
        op[308] = getOpcodeRnm(43, 316); // isegment
        {int[] a = {310,311}; op[309] = getOpcodeCat(a);}
        op[310] = getOpcodeRnm(44, 318); // isegment-nz
        op[311] = getOpcodeRep((char)0, Character.MAX_VALUE, 312);
        {int[] a = {313,314}; op[312] = getOpcodeCat(a);}
        {char[] a = {47}; op[313] = getOpcodeTls(a);}
        op[314] = getOpcodeRnm(43, 316); // isegment
        {char[] a = {}; op[315] = getOpcodeTls(a);}
        op[316] = getOpcodeRep((char)0, Character.MAX_VALUE, 317);
        op[317] = getOpcodeRnm(48, 332); // ipchar
        op[318] = getOpcodeRep((char)1, Character.MAX_VALUE, 319);
        op[319] = getOpcodeRnm(48, 332); // ipchar
        op[320] = getOpcodeRep((char)0, Character.MAX_VALUE, 321);
        {int[] a = {322,323,324,325}; op[321] = getOpcodeAlt(a);}
        op[322] = getOpcodeRnm(48, 332); // ipchar
        op[323] = getOpcodeRnm(46, 326); // iprivate
        {char[] a = {47}; op[324] = getOpcodeTls(a);}
        {char[] a = {63}; op[325] = getOpcodeTls(a);}
        op[326] = getOpcodeTrg((char)57344, (char)63743);
        op[327] = getOpcodeRep((char)0, Character.MAX_VALUE, 328);
        {int[] a = {329,330,331}; op[328] = getOpcodeAlt(a);}
        op[329] = getOpcodeRnm(48, 332); // ipchar
        {char[] a = {47}; op[330] = getOpcodeTls(a);}
        {char[] a = {63}; op[331] = getOpcodeTls(a);}
        {int[] a = {333,334,335,336,337}; op[332] = getOpcodeAlt(a);}
        op[333] = getOpcodeRnm(49, 338); // iunreserved
        op[334] = getOpcodeRnm(50, 346); // pct-encoded
        op[335] = getOpcodeRnm(54, 365); // sub-delims
        {char[] a = {58}; op[336] = getOpcodeTls(a);}
        {char[] a = {64}; op[337] = getOpcodeTls(a);}
        {int[] a = {339,340,341,342,343,344,345}; op[338] = getOpcodeAlt(a);}
        op[339] = getOpcodeRnm(56, 382); // ALPHA
        op[340] = getOpcodeRnm(62, 396); // DIGIT
        {char[] a = {45}; op[341] = getOpcodeTls(a);}
        {char[] a = {46}; op[342] = getOpcodeTls(a);}
        {char[] a = {95}; op[343] = getOpcodeTls(a);}
        {char[] a = {126}; op[344] = getOpcodeTls(a);}
        op[345] = getOpcodeRnm(51, 350); // ucschar
        {int[] a = {347,348,349}; op[346] = getOpcodeCat(a);}
        {char[] a = {37}; op[347] = getOpcodeTls(a);}
        op[348] = getOpcodeRnm(64, 398); // HEXDIG
        op[349] = getOpcodeRnm(64, 398); // HEXDIG
        {int[] a = {351,352,353}; op[350] = getOpcodeAlt(a);}
        op[351] = getOpcodeTrg((char)160, (char)55295);
        op[352] = getOpcodeTrg((char)63744, (char)64975);
        op[353] = getOpcodeTrg((char)65008, (char)65519);
        {int[] a = {355,356}; op[354] = getOpcodeAlt(a);}
        op[355] = getOpcodeRnm(53, 357); // gen-delims
        op[356] = getOpcodeRnm(54, 365); // sub-delims
        {int[] a = {358,359,360,361,362,363,364}; op[357] = getOpcodeAlt(a);}
        {char[] a = {58}; op[358] = getOpcodeTls(a);}
        {char[] a = {47}; op[359] = getOpcodeTls(a);}
        {char[] a = {63}; op[360] = getOpcodeTls(a);}
        {char[] a = {35}; op[361] = getOpcodeTls(a);}
        {char[] a = {91}; op[362] = getOpcodeTls(a);}
        {char[] a = {93}; op[363] = getOpcodeTls(a);}
        {char[] a = {64}; op[364] = getOpcodeTls(a);}
        {int[] a = {366,367,368,369,370,371,372,373,374}; op[365] = getOpcodeAlt(a);}
        {char[] a = {33}; op[366] = getOpcodeTls(a);}
        {char[] a = {36}; op[367] = getOpcodeTls(a);}
        {char[] a = {38}; op[368] = getOpcodeTls(a);}
        {char[] a = {39}; op[369] = getOpcodeTls(a);}
        {char[] a = {42}; op[370] = getOpcodeTls(a);}
        {char[] a = {43}; op[371] = getOpcodeTls(a);}
        {char[] a = {44}; op[372] = getOpcodeTls(a);}
        {char[] a = {59}; op[373] = getOpcodeTls(a);}
        {char[] a = {61}; op[374] = getOpcodeTls(a);}
        {int[] a = {376,377,378,379,380,381}; op[375] = getOpcodeAlt(a);}
        op[376] = getOpcodeRnm(56, 382); // ALPHA
        op[377] = getOpcodeRnm(62, 396); // DIGIT
        {char[] a = {45}; op[378] = getOpcodeTls(a);}
        {char[] a = {46}; op[379] = getOpcodeTls(a);}
        {char[] a = {95}; op[380] = getOpcodeTls(a);}
        {char[] a = {126}; op[381] = getOpcodeTls(a);}
        {int[] a = {383,384}; op[382] = getOpcodeAlt(a);}
        op[383] = getOpcodeTrg((char)65, (char)90);
        op[384] = getOpcodeTrg((char)97, (char)122);
        {int[] a = {386,387}; op[385] = getOpcodeAlt(a);}
        {char[] a = {48}; op[386] = getOpcodeTls(a);}
        {char[] a = {49}; op[387] = getOpcodeTls(a);}
        op[388] = getOpcodeTrg((char)1, (char)127);
        {char[] a = {13}; op[389] = getOpcodeTbs(a);}
        {int[] a = {391,392}; op[390] = getOpcodeCat(a);}
        op[391] = getOpcodeRnm(59, 389); // CR
        op[392] = getOpcodeRnm(66, 407); // LF
        {int[] a = {394,395}; op[393] = getOpcodeAlt(a);}
        op[394] = getOpcodeTrg((char)0, (char)31);
        {char[] a = {127}; op[395] = getOpcodeTbs(a);}
        op[396] = getOpcodeTrg((char)48, (char)57);
        {char[] a = {34}; op[397] = getOpcodeTbs(a);}
        {int[] a = {399,400,401,402,403,404,405}; op[398] = getOpcodeAlt(a);}
        op[399] = getOpcodeRnm(62, 396); // DIGIT
        {char[] a = {65}; op[400] = getOpcodeTls(a);}
        {char[] a = {66}; op[401] = getOpcodeTls(a);}
        {char[] a = {67}; op[402] = getOpcodeTls(a);}
        {char[] a = {68}; op[403] = getOpcodeTls(a);}
        {char[] a = {69}; op[404] = getOpcodeTls(a);}
        {char[] a = {70}; op[405] = getOpcodeTls(a);}
        {char[] a = {9}; op[406] = getOpcodeTbs(a);}
        {char[] a = {10}; op[407] = getOpcodeTbs(a);}
        op[408] = getOpcodeRep((char)0, Character.MAX_VALUE, 409);
        {int[] a = {410,411}; op[409] = getOpcodeAlt(a);}
        op[410] = getOpcodeRnm(71, 417); // WSP
        {int[] a = {412,413}; op[411] = getOpcodeCat(a);}
        op[412] = getOpcodeRnm(60, 390); // CRLF
        op[413] = getOpcodeRnm(71, 417); // WSP
        op[414] = getOpcodeTrg((char)0, (char)255);
        {char[] a = {32}; op[415] = getOpcodeTbs(a);}
        op[416] = getOpcodeTrg((char)33, (char)126);
        {int[] a = {418,419}; op[417] = getOpcodeAlt(a);}
        op[418] = getOpcodeRnm(69, 415); // SP
        op[419] = getOpcodeRnm(65, 406); // HTAB
        return op;
    }

    public static void display(PrintStream out){
        out.println(";");
        out.println("; xdi2.core.xri3.parser.apg.XDI3Grammar");
        out.println(";");
        out.println(";");
        out.println("; This file contains ABNF rules from the following sources:");
        out.println("; http://wiki.oasis-open.org/xri/XriThree/SyntaxAbnf");
        out.println("; http://wiki.oasis-open.org/xdi/XdiAbnf");
        out.println(";");
        out.println("; A few modifications have been made to the rules:");
        out.println(";");
        out.println("; 1) APG doesn't support prose-val. the \"ipath-empty\" rule has been adjusted accordingly.");
        out.println("; 2) Java regular expressions only support the BMP of unicode. The \"ucschar\" and \"iprivate\"");
        out.println(";    rules have been adjusted accordingly.");
        out.println("; 3) The \"(\" and \")\" characters have been removed from the sub-delims rule to avoid a conflict with the xref rule.");
        out.println(";");
        out.println("; =markus 01-25-2013");
        out.println(";");
        out.println("");
        out.println(";");
        out.println("; XDI rules");
        out.println(";");
        out.println("");
        out.println("xdi-address = xdi-subject / xdi-statement");
        out.println("xdi-statement = xdi-subject \"/\" xdi-predicate \"/\" xdi-object");
        out.println("xdi-inner = xdi-subject \"/\" xdi-predicate");
        out.println("xdi-subject = xdi-segment");
        out.println("xdi-predicate = xdi-segment");
        out.println("xdi-object = xdi-segment");
        out.println("xdi-segment = ( literal / subseg ) *subseg");
        out.println("subseg = global-subseg / local-subseg / xref");
        out.println("global-subseg = gcs-char [ local-subseg / xref / literal ]");
        out.println("local-subseg = lcs-char [ xref / literal ]");
        out.println("gcs-char          = \"=\" / \"@\" / \"+\" / \"$\"");
        out.println("lcs-char          = \"*\" / \"!\"");
        out.println("xref = xref-empty / xref-IRI / xref-subject / xref-inner / xref-statement");
        out.println("xref-empty = \"()\"");
        out.println("xref-IRI = \"(\" IRI \")\"");
        out.println("xref-subject = \"(\" xdi-subject \")\"");
        out.println("xref-inner = \"(\" xdi-inner \")\"");
        out.println("xref-statement = \"(\" xdi-statement \")\"");
        out.println("literal           = 1*xdi-pchar");
        out.println("xdi-pchar         = iunreserved / pct-encoded / xdi-sub-delims / \":\"");
        out.println("xdi-pchar-nc      = iunreserved / pct-encoded / xdi-sub-delims");
        out.println("xdi-reserved      = xdi-gen-delims / xdi-sub-delims");
        out.println("xdi-gen-delims    = \":\" / \"/\" / \"?\" / \"#\" / \"[\" / \"]\" / \"(\" / \")\" / gcs-char / lcs-char");
        out.println("xdi-sub-delims    = \"&\" / \";\" / \",\" / \"'\"");
        out.println("");
        out.println(";");
        out.println("; IRI rules");
        out.println(";");
        out.println("");
        out.println("IRI               = scheme \":\" ihier-part [ \"?\" iquery ]");
        out.println("                  [ \"#\" ifragment ]");
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
        out.println("                  / \"25\" %x30-35         ; 250-255");
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
        out.println("gen-delims        = \":\" / \"/\" / \"?\" / \"#\" / \"[\" / \"]\" / \"@\"");
        out.println("sub-delims        = \"!\" / \"$\" / \"&\" / \"'\"");
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
