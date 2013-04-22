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
    public static int ruleCount = 94;
    public enum RuleNames{
        ABSOLUTE("absolute", 5, 23, 4),
        ABSOLUTE_INVERSE("absolute-inverse", 9, 39, 4),
        ALPHA("ALPHA", 89, 382, 3),
        ATTRIBUTE_CLASS("attribute-class", 58, 237, 4),
        ATTRIBUTE_DEFINITION("attribute-definition", 69, 287, 4),
        ATTRIBUTE_PATH("attribute-path", 24, 113, 4),
        ATTRIBUTE_SINGLETON("attribute-singleton", 42, 197, 4),
        AUTHORITY_CLASS("authority-class", 49, 216, 3),
        AUTHORITY_DEFINITION("authority-definition", 65, 270, 6),
        AUTHORITY_PATH("authority-path", 66, 276, 4),
        AUTHORITY_SINGLETON("authority-singleton", 36, 167, 3),
        CLASS("class", 43, 201, 3),
        CLASS_CONTEXT("class-context", 19, 87, 5),
        CLASS_INVERSE("class-inverse", 12, 51, 4),
        CLASS_PATH("class-path", 20, 92, 5),
        CLASS_RELATIVE("class-relative", 8, 35, 4),
        CLOCK_SEQ("clock-seq", 82, 349, 2),
        CLOCK_SEQ_LOW("clock-seq-low", 83, 351, 2),
        CONCRETE_CLASS("concrete-class", 47, 209, 3),
        CONTEXT("context", 17, 78, 7),
        CONTEXT_INVERSE("context-inverse", 11, 47, 4),
        CONTEXT_RELATIVE("context-relative", 7, 31, 4),
        CONTEXT_SYMBOL("context-symbol", 87, 367, 7),
        CONTEXTUAL_STATEMENT("contextual-statement", 2, 10, 3),
        CRLF("CRLF", 92, 389, 6),
        DEFINITION("definition", 64, 267, 3),
        DIGIT("DIGIT", 90, 385, 1),
        DIRECT_CONTEXTUAL("direct-contextual", 3, 13, 5),
        DIRECT_RELATIONAL("direct-relational", 14, 58, 6),
        DQUOTE("DQUOTE", 93, 395, 1),
        ENTITY_CLASS("entity-class", 48, 212, 4),
        ENTITY_DEFINITION("entity-definition", 68, 283, 4),
        ENTITY_SINGLETON("entity-singleton", 35, 164, 3),
        GROUP_CLASS("group-class", 55, 234, 1),
        GROUP_SINGLETON("group-singleton", 39, 179, 6),
        HEXDIG("HEXDIG", 91, 386, 3),
        IMMUTABLE_ID("immutable-id", 63, 259, 8),
        IMMUTABLE_ID_CLASS("immutable-id-class", 57, 236, 1),
        INNER_ROOT("inner-root", 29, 126, 6),
        INSTANCE("instance", 59, 241, 3),
        INSTANCE_CLASS("instance-class", 51, 222, 3),
        INSTANCE_CONTEXT("instance-context", 21, 97, 5),
        INVERSE_CONTEXTUAL("inverse-contextual", 4, 18, 5),
        INVERSE_RELATIONAL("inverse-relational", 15, 64, 6),
        IPV6_LITERAL("ipv6-literal", 77, 322, 9),
        IRI_CHAR("iri-char", 85, 355, 4),
        JSON_ARRAY("json-array", 75, 312, 5),
        JSON_BOOLEAN("json-boolean", 74, 309, 3),
        JSON_NUMBER("json-number", 73, 307, 2),
        JSON_OBJECT("json-object", 76, 317, 5),
        JSON_STRING("json-string", 72, 302, 5),
        JSON_VALUE("json-value", 71, 296, 6),
        LITERAL_CONTEXT("literal-context", 22, 102, 5),
        LITERAL_PATH("literal-path", 23, 107, 6),
        LITERAL_STATEMENT("literal-statement", 16, 70, 8),
        META_CLASS("meta-class", 44, 204, 3),
        MUTABLE_ID("mutable-id", 62, 251, 8),
        MUTABLE_ID_CLASS("mutable-id-class", 56, 235, 1),
        NODE("node", 84, 353, 2),
        NONPAREN_DELIM("nonparen-delim", 86, 359, 8),
        ORDERED_INSTANCE("ordered-instance", 60, 244, 4),
        OUTER_ROOT("outer-root", 26, 120, 1),
        PEER_ROOT("peer-root", 28, 125, 1),
        PERSON_CLASS("person-class", 54, 233, 1),
        PERSON_SINGLETON("person-singleton", 38, 173, 6),
        RELATIONAL_STATEMENT("relational-statement", 13, 55, 3),
        RELATIVE_CONTEXT("relative-context", 18, 85, 2),
        RELATIVE_ROOT("relative-root", 27, 121, 4),
        RESERVED_CLASS("reserved-class", 52, 225, 4),
        RESERVED_META_CLASS("reserved-meta-class", 45, 207, 1),
        RESERVED_TYPE("reserved-type", 40, 185, 4),
        ROOT("root", 25, 117, 3),
        ROOT_INVERSE("root-inverse", 10, 43, 4),
        ROOT_RELATIVE("root-relative", 6, 27, 4),
        SINGLETON("singleton", 34, 161, 3),
        STATEMENT_ROOT("statement-root", 30, 132, 4),
        SUBPATH("subpath", 32, 145, 9),
        SUBSEGMENT("subsegment", 33, 154, 7),
        TIME_HIGH("time-high", 81, 347, 2),
        TIME_LOW("time-low", 79, 343, 2),
        TIME_MID("time-mid", 80, 345, 2),
        TYPE_CLASS("type-class", 50, 219, 3),
        TYPE_DEFINITION("type-definition", 67, 280, 3),
        TYPE_SINGLETON("type-singleton", 37, 170, 3),
        UNORDERED_INSTANCE("unordered-instance", 61, 248, 3),
        UNRESERVED_CLASS("unreserved-class", 53, 229, 4),
        UNRESERVED_META_CLASS("unreserved-meta-class", 46, 208, 1),
        UNRESERVED_TYPE("unreserved-type", 41, 189, 8),
        UUID_LITERAL("uuid-literal", 78, 331, 12),
        VARIABLE("variable", 70, 291, 5),
        XDI_CHAR("xdi-char", 88, 374, 8),
        XDI_GRAPH("xdi-graph", 0, 0, 6),
        XDI_STATEMENT("xdi-statement", 1, 6, 4),
        XREF("xref", 31, 136, 9);
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
    	Rule[] rules = new Rule[94];
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
    	Opcode[] op = new Opcode[396];
        {int[] a = {1,2}; op[0] = getOpcodeCat(a);}
        op[1] = getOpcodeRnm(1, 6); // xdi-statement
        op[2] = getOpcodeRep((char)0, Character.MAX_VALUE, 3);
        {int[] a = {4,5}; op[3] = getOpcodeCat(a);}
        op[4] = getOpcodeRnm(92, 389); // CRLF
        op[5] = getOpcodeRnm(1, 6); // xdi-statement
        {int[] a = {7,8,9}; op[6] = getOpcodeAlt(a);}
        op[7] = getOpcodeRnm(2, 10); // contextual-statement
        op[8] = getOpcodeRnm(13, 55); // relational-statement
        op[9] = getOpcodeRnm(16, 70); // literal-statement
        {int[] a = {11,12}; op[10] = getOpcodeAlt(a);}
        op[11] = getOpcodeRnm(3, 13); // direct-contextual
        op[12] = getOpcodeRnm(4, 18); // inverse-contextual
        {int[] a = {14,15,16,17}; op[13] = getOpcodeAlt(a);}
        op[14] = getOpcodeRnm(5, 23); // absolute
        op[15] = getOpcodeRnm(6, 27); // root-relative
        op[16] = getOpcodeRnm(7, 31); // context-relative
        op[17] = getOpcodeRnm(8, 35); // class-relative
        {int[] a = {19,20,21,22}; op[18] = getOpcodeAlt(a);}
        op[19] = getOpcodeRnm(9, 39); // absolute-inverse
        op[20] = getOpcodeRnm(10, 43); // root-inverse
        op[21] = getOpcodeRnm(11, 47); // context-inverse
        op[22] = getOpcodeRnm(12, 51); // class-inverse
        {int[] a = {24,25,26}; op[23] = getOpcodeCat(a);}
        op[24] = getOpcodeRnm(26, 120); // outer-root
        {char[] a = {47,40,41,47}; op[25] = getOpcodeTls(a);}
        op[26] = getOpcodeRnm(17, 78); // context
        {int[] a = {28,29,30}; op[27] = getOpcodeCat(a);}
        op[28] = getOpcodeRnm(27, 121); // relative-root
        {char[] a = {47,40,41,47}; op[29] = getOpcodeTls(a);}
        op[30] = getOpcodeRnm(17, 78); // context
        {int[] a = {32,33,34}; op[31] = getOpcodeCat(a);}
        op[32] = getOpcodeRnm(17, 78); // context
        {char[] a = {47,40,41,47}; op[33] = getOpcodeTls(a);}
        op[34] = getOpcodeRnm(18, 85); // relative-context
        {int[] a = {36,37,38}; op[35] = getOpcodeCat(a);}
        op[36] = getOpcodeRnm(19, 87); // class-context
        {char[] a = {47,40,41,47}; op[37] = getOpcodeTls(a);}
        op[38] = getOpcodeRnm(21, 97); // instance-context
        {int[] a = {40,41,42}; op[39] = getOpcodeCat(a);}
        op[40] = getOpcodeRnm(17, 78); // context
        {char[] a = {47,36,105,115,40,41,47}; op[41] = getOpcodeTls(a);}
        op[42] = getOpcodeRnm(26, 120); // outer-root
        {int[] a = {44,45,46}; op[43] = getOpcodeCat(a);}
        op[44] = getOpcodeRnm(17, 78); // context
        {char[] a = {47,36,105,115,40,41,47}; op[45] = getOpcodeTls(a);}
        op[46] = getOpcodeRnm(27, 121); // relative-root
        {int[] a = {48,49,50}; op[47] = getOpcodeCat(a);}
        op[48] = getOpcodeRnm(18, 85); // relative-context
        {char[] a = {47,36,105,115,40,41,47}; op[49] = getOpcodeTls(a);}
        op[50] = getOpcodeRnm(17, 78); // context
        {int[] a = {52,53,54}; op[51] = getOpcodeCat(a);}
        op[52] = getOpcodeRnm(21, 97); // instance-context
        {char[] a = {47,36,105,115,40,41,47}; op[53] = getOpcodeTls(a);}
        op[54] = getOpcodeRnm(19, 87); // class-context
        {int[] a = {56,57}; op[55] = getOpcodeAlt(a);}
        op[56] = getOpcodeRnm(15, 64); // inverse-relational
        op[57] = getOpcodeRnm(14, 58); // direct-relational
        {int[] a = {59,60,61,62,63}; op[58] = getOpcodeCat(a);}
        op[59] = getOpcodeRnm(17, 78); // context
        {char[] a = {47}; op[60] = getOpcodeTls(a);}
        op[61] = getOpcodeRnm(17, 78); // context
        {char[] a = {47}; op[62] = getOpcodeTls(a);}
        op[63] = getOpcodeRnm(17, 78); // context
        {int[] a = {65,66,67,68,69}; op[64] = getOpcodeCat(a);}
        op[65] = getOpcodeRnm(17, 78); // context
        {char[] a = {47,36,105,115}; op[66] = getOpcodeTls(a);}
        op[67] = getOpcodeRnm(17, 78); // context
        {char[] a = {47}; op[68] = getOpcodeTls(a);}
        op[69] = getOpcodeRnm(17, 78); // context
        {int[] a = {71,72,73,76,77}; op[70] = getOpcodeCat(a);}
        op[71] = getOpcodeRnm(22, 102); // literal-context
        {char[] a = {38,47}; op[72] = getOpcodeTls(a);}
        op[73] = getOpcodeRep((char)0, (char)1, 74);
        op[74] = getOpcodeRep((char)0, Character.MAX_VALUE, 75);
        op[75] = getOpcodeRnm(37, 170); // type-singleton
        {char[] a = {38,47}; op[76] = getOpcodeTls(a);}
        op[77] = getOpcodeRnm(71, 296); // json-value
        {int[] a = {79,84}; op[78] = getOpcodeAlt(a);}
        {int[] a = {80,82}; op[79] = getOpcodeCat(a);}
        op[80] = getOpcodeRep((char)1, Character.MAX_VALUE, 81);
        op[81] = getOpcodeRnm(27, 121); // relative-root
        op[82] = getOpcodeRep((char)0, (char)1, 83);
        op[83] = getOpcodeRnm(18, 85); // relative-context
        op[84] = getOpcodeRnm(18, 85); // relative-context
        op[85] = getOpcodeRep((char)1, Character.MAX_VALUE, 86);
        op[86] = getOpcodeRnm(32, 145); // subpath
        {int[] a = {88,91}; op[87] = getOpcodeCat(a);}
        op[88] = getOpcodeRep((char)0, (char)1, 89);
        op[89] = getOpcodeRep((char)1, Character.MAX_VALUE, 90);
        op[90] = getOpcodeRnm(27, 121); // relative-root
        op[91] = getOpcodeRnm(20, 92); // class-path
        {int[] a = {93,96}; op[92] = getOpcodeAlt(a);}
        {int[] a = {94,95}; op[93] = getOpcodeCat(a);}
        op[94] = getOpcodeRnm(32, 145); // subpath
        op[95] = getOpcodeRnm(20, 92); // class-path
        op[96] = getOpcodeRnm(43, 201); // class
        {int[] a = {98,100}; op[97] = getOpcodeCat(a);}
        op[98] = getOpcodeRep((char)1, Character.MAX_VALUE, 99);
        op[99] = getOpcodeRnm(59, 241); // instance
        op[100] = getOpcodeRep((char)0, (char)1, 101);
        op[101] = getOpcodeRnm(18, 85); // relative-context
        {int[] a = {103,106}; op[102] = getOpcodeCat(a);}
        op[103] = getOpcodeRep((char)0, (char)1, 104);
        op[104] = getOpcodeRep((char)1, Character.MAX_VALUE, 105);
        op[105] = getOpcodeRnm(27, 121); // relative-root
        op[106] = getOpcodeRnm(23, 107); // literal-path
        {int[] a = {108,111,112}; op[107] = getOpcodeAlt(a);}
        {int[] a = {109,110}; op[108] = getOpcodeCat(a);}
        op[109] = getOpcodeRnm(32, 145); // subpath
        op[110] = getOpcodeRnm(23, 107); // literal-path
        op[111] = getOpcodeRnm(42, 197); // attribute-singleton
        op[112] = getOpcodeRnm(24, 113); // attribute-path
        {int[] a = {114,115}; op[113] = getOpcodeCat(a);}
        op[114] = getOpcodeRnm(58, 237); // attribute-class
        op[115] = getOpcodeRep((char)1, Character.MAX_VALUE, 116);
        op[116] = getOpcodeRnm(59, 241); // instance
        {int[] a = {118,119}; op[117] = getOpcodeAlt(a);}
        op[118] = getOpcodeRnm(26, 120); // outer-root
        op[119] = getOpcodeRnm(27, 121); // relative-root
        {char[] a = {40,41}; op[120] = getOpcodeTls(a);}
        {int[] a = {122,123,124}; op[121] = getOpcodeAlt(a);}
        op[122] = getOpcodeRnm(28, 125); // peer-root
        op[123] = getOpcodeRnm(29, 126); // inner-root
        op[124] = getOpcodeRnm(30, 132); // statement-root
        op[125] = getOpcodeRnm(31, 136); // xref
        {int[] a = {127,128,129,130,131}; op[126] = getOpcodeCat(a);}
        {char[] a = {40}; op[127] = getOpcodeTls(a);}
        op[128] = getOpcodeRnm(17, 78); // context
        {char[] a = {47}; op[129] = getOpcodeTls(a);}
        op[130] = getOpcodeRnm(17, 78); // context
        {char[] a = {41}; op[131] = getOpcodeTls(a);}
        {int[] a = {133,134,135}; op[132] = getOpcodeCat(a);}
        {char[] a = {40}; op[133] = getOpcodeTls(a);}
        op[134] = getOpcodeRnm(1, 6); // xdi-statement
        {char[] a = {41}; op[135] = getOpcodeTls(a);}
        {int[] a = {137,138,144}; op[136] = getOpcodeCat(a);}
        {char[] a = {40}; op[137] = getOpcodeTls(a);}
        {int[] a = {139,140,142}; op[138] = getOpcodeAlt(a);}
        op[139] = getOpcodeRnm(17, 78); // context
        op[140] = getOpcodeRep((char)1, Character.MAX_VALUE, 141);
        op[141] = getOpcodeRnm(85, 355); // iri-char
        op[142] = getOpcodeRep((char)1, Character.MAX_VALUE, 143);
        op[143] = getOpcodeRnm(88, 374); // xdi-char
        {char[] a = {41}; op[144] = getOpcodeTls(a);}
        {int[] a = {146,147,152,153}; op[145] = getOpcodeAlt(a);}
        op[146] = getOpcodeRnm(34, 161); // singleton
        {int[] a = {148,149}; op[147] = getOpcodeCat(a);}
        op[148] = getOpcodeRnm(43, 201); // class
        op[149] = getOpcodeRep((char)0, (char)1, 150);
        op[150] = getOpcodeRep((char)1, Character.MAX_VALUE, 151);
        op[151] = getOpcodeRnm(59, 241); // instance
        op[152] = getOpcodeRnm(64, 267); // definition
        op[153] = getOpcodeRnm(70, 291); // variable
        {int[] a = {155,156,157,158,159,160}; op[154] = getOpcodeAlt(a);}
        op[155] = getOpcodeRnm(25, 117); // root
        op[156] = getOpcodeRnm(34, 161); // singleton
        op[157] = getOpcodeRnm(43, 201); // class
        op[158] = getOpcodeRnm(59, 241); // instance
        op[159] = getOpcodeRnm(64, 267); // definition
        op[160] = getOpcodeRnm(70, 291); // variable
        {int[] a = {162,163}; op[161] = getOpcodeAlt(a);}
        op[162] = getOpcodeRnm(35, 164); // entity-singleton
        op[163] = getOpcodeRnm(42, 197); // attribute-singleton
        {int[] a = {165,166}; op[164] = getOpcodeAlt(a);}
        op[165] = getOpcodeRnm(36, 167); // authority-singleton
        op[166] = getOpcodeRnm(37, 170); // type-singleton
        {int[] a = {168,169}; op[167] = getOpcodeAlt(a);}
        op[168] = getOpcodeRnm(38, 173); // person-singleton
        op[169] = getOpcodeRnm(39, 179); // group-singleton
        {int[] a = {171,172}; op[170] = getOpcodeAlt(a);}
        op[171] = getOpcodeRnm(40, 185); // reserved-type
        op[172] = getOpcodeRnm(41, 189); // unreserved-type
        {int[] a = {174,175}; op[173] = getOpcodeCat(a);}
        {char[] a = {61}; op[174] = getOpcodeTls(a);}
        {int[] a = {176,177}; op[175] = getOpcodeAlt(a);}
        op[176] = getOpcodeRnm(31, 136); // xref
        op[177] = getOpcodeRep((char)1, Character.MAX_VALUE, 178);
        op[178] = getOpcodeRnm(88, 374); // xdi-char
        {int[] a = {180,181}; op[179] = getOpcodeCat(a);}
        {char[] a = {64}; op[180] = getOpcodeTls(a);}
        {int[] a = {182,183}; op[181] = getOpcodeAlt(a);}
        op[182] = getOpcodeRnm(31, 136); // xref
        op[183] = getOpcodeRep((char)1, Character.MAX_VALUE, 184);
        op[184] = getOpcodeRnm(88, 374); // xdi-char
        {int[] a = {186,187}; op[185] = getOpcodeCat(a);}
        {char[] a = {36}; op[186] = getOpcodeTls(a);}
        op[187] = getOpcodeRep((char)1, Character.MAX_VALUE, 188);
        op[188] = getOpcodeRnm(88, 374); // xdi-char
        {int[] a = {190,191}; op[189] = getOpcodeCat(a);}
        {char[] a = {43}; op[190] = getOpcodeTls(a);}
        {int[] a = {192,193,194,195}; op[191] = getOpcodeAlt(a);}
        op[192] = getOpcodeRnm(31, 136); // xref
        op[193] = getOpcodeRnm(78, 331); // uuid-literal
        op[194] = getOpcodeRnm(77, 322); // ipv6-literal
        op[195] = getOpcodeRep((char)1, Character.MAX_VALUE, 196);
        op[196] = getOpcodeRnm(88, 374); // xdi-char
        {int[] a = {198,199,200}; op[197] = getOpcodeCat(a);}
        {char[] a = {60}; op[198] = getOpcodeTls(a);}
        op[199] = getOpcodeRnm(37, 170); // type-singleton
        {char[] a = {62}; op[200] = getOpcodeTls(a);}
        {int[] a = {202,203}; op[201] = getOpcodeAlt(a);}
        op[202] = getOpcodeRnm(44, 204); // meta-class
        op[203] = getOpcodeRnm(47, 209); // concrete-class
        {int[] a = {205,206}; op[204] = getOpcodeAlt(a);}
        op[205] = getOpcodeRnm(45, 207); // reserved-meta-class
        op[206] = getOpcodeRnm(46, 208); // unreserved-meta-class
        {char[] a = {91,36,93}; op[207] = getOpcodeTls(a);}
        {char[] a = {91,43,93}; op[208] = getOpcodeTls(a);}
        {int[] a = {210,211}; op[209] = getOpcodeAlt(a);}
        op[210] = getOpcodeRnm(48, 212); // entity-class
        op[211] = getOpcodeRnm(58, 237); // attribute-class
        {int[] a = {213,214,215}; op[212] = getOpcodeAlt(a);}
        op[213] = getOpcodeRnm(49, 216); // authority-class
        op[214] = getOpcodeRnm(50, 219); // type-class
        op[215] = getOpcodeRnm(51, 222); // instance-class
        {int[] a = {217,218}; op[216] = getOpcodeAlt(a);}
        op[217] = getOpcodeRnm(54, 233); // person-class
        op[218] = getOpcodeRnm(55, 234); // group-class
        {int[] a = {220,221}; op[219] = getOpcodeAlt(a);}
        op[220] = getOpcodeRnm(52, 225); // reserved-class
        op[221] = getOpcodeRnm(53, 229); // unreserved-class
        {int[] a = {223,224}; op[222] = getOpcodeAlt(a);}
        op[223] = getOpcodeRnm(56, 235); // mutable-id-class
        op[224] = getOpcodeRnm(57, 236); // immutable-id-class
        {int[] a = {226,227,228}; op[225] = getOpcodeCat(a);}
        {char[] a = {91}; op[226] = getOpcodeTls(a);}
        op[227] = getOpcodeRnm(40, 185); // reserved-type
        {char[] a = {93}; op[228] = getOpcodeTls(a);}
        {int[] a = {230,231,232}; op[229] = getOpcodeCat(a);}
        {char[] a = {91}; op[230] = getOpcodeTls(a);}
        op[231] = getOpcodeRnm(41, 189); // unreserved-type
        {char[] a = {93}; op[232] = getOpcodeTls(a);}
        {char[] a = {91,61,93}; op[233] = getOpcodeTls(a);}
        {char[] a = {91,64,93}; op[234] = getOpcodeTls(a);}
        {char[] a = {91,42,93}; op[235] = getOpcodeTls(a);}
        {char[] a = {91,33,93}; op[236] = getOpcodeTls(a);}
        {int[] a = {238,239,240}; op[237] = getOpcodeCat(a);}
        {char[] a = {91}; op[238] = getOpcodeTls(a);}
        op[239] = getOpcodeRnm(42, 197); // attribute-singleton
        {char[] a = {93}; op[240] = getOpcodeTls(a);}
        {int[] a = {242,243}; op[241] = getOpcodeAlt(a);}
        op[242] = getOpcodeRnm(60, 244); // ordered-instance
        op[243] = getOpcodeRnm(61, 248); // unordered-instance
        {int[] a = {245,246}; op[244] = getOpcodeCat(a);}
        {char[] a = {35}; op[245] = getOpcodeTls(a);}
        op[246] = getOpcodeRep((char)1, Character.MAX_VALUE, 247);
        op[247] = getOpcodeRnm(90, 385); // DIGIT
        {int[] a = {249,250}; op[248] = getOpcodeAlt(a);}
        op[249] = getOpcodeRnm(62, 251); // mutable-id
        op[250] = getOpcodeRnm(63, 259); // immutable-id
        {int[] a = {252,253}; op[251] = getOpcodeCat(a);}
        {char[] a = {42}; op[252] = getOpcodeTls(a);}
        {int[] a = {254,255,256,257}; op[253] = getOpcodeAlt(a);}
        op[254] = getOpcodeRnm(31, 136); // xref
        op[255] = getOpcodeRnm(78, 331); // uuid-literal
        op[256] = getOpcodeRnm(77, 322); // ipv6-literal
        op[257] = getOpcodeRep((char)1, Character.MAX_VALUE, 258);
        op[258] = getOpcodeRnm(88, 374); // xdi-char
        {int[] a = {260,261}; op[259] = getOpcodeCat(a);}
        {char[] a = {33}; op[260] = getOpcodeTls(a);}
        {int[] a = {262,263,264,265}; op[261] = getOpcodeAlt(a);}
        op[262] = getOpcodeRnm(31, 136); // xref
        op[263] = getOpcodeRnm(78, 331); // uuid-literal
        op[264] = getOpcodeRnm(77, 322); // ipv6-literal
        op[265] = getOpcodeRep((char)1, Character.MAX_VALUE, 266);
        op[266] = getOpcodeRnm(88, 374); // xdi-char
        {int[] a = {268,269}; op[267] = getOpcodeAlt(a);}
        op[268] = getOpcodeRnm(65, 270); // authority-definition
        op[269] = getOpcodeRnm(67, 280); // type-definition
        {int[] a = {271,272,275}; op[270] = getOpcodeCat(a);}
        {char[] a = {36,40}; op[271] = getOpcodeTls(a);}
        {int[] a = {273,274}; op[272] = getOpcodeAlt(a);}
        op[273] = getOpcodeRnm(36, 167); // authority-singleton
        op[274] = getOpcodeRnm(66, 276); // authority-path
        {char[] a = {41}; op[275] = getOpcodeTls(a);}
        {int[] a = {277,278}; op[276] = getOpcodeCat(a);}
        op[277] = getOpcodeRnm(49, 216); // authority-class
        op[278] = getOpcodeRep((char)1, Character.MAX_VALUE, 279);
        op[279] = getOpcodeRnm(59, 241); // instance
        {int[] a = {281,282}; op[280] = getOpcodeAlt(a);}
        op[281] = getOpcodeRnm(68, 283); // entity-definition
        op[282] = getOpcodeRnm(69, 287); // attribute-definition
        {int[] a = {284,285,286}; op[283] = getOpcodeCat(a);}
        {char[] a = {36,40}; op[284] = getOpcodeTls(a);}
        op[285] = getOpcodeRnm(37, 170); // type-singleton
        {char[] a = {41}; op[286] = getOpcodeTls(a);}
        {int[] a = {288,289,290}; op[287] = getOpcodeCat(a);}
        {char[] a = {36,40}; op[288] = getOpcodeTls(a);}
        op[289] = getOpcodeRnm(42, 197); // attribute-singleton
        {char[] a = {41}; op[290] = getOpcodeTls(a);}
        {int[] a = {292,293,295}; op[291] = getOpcodeCat(a);}
        {char[] a = {123}; op[292] = getOpcodeTls(a);}
        op[293] = getOpcodeRep((char)0, (char)1, 294);
        op[294] = getOpcodeRnm(17, 78); // context
        {char[] a = {125}; op[295] = getOpcodeTls(a);}
        {int[] a = {297,298,299,300,301}; op[296] = getOpcodeAlt(a);}
        op[297] = getOpcodeRnm(72, 302); // json-string
        op[298] = getOpcodeRnm(73, 307); // json-number
        op[299] = getOpcodeRnm(74, 309); // json-boolean
        op[300] = getOpcodeRnm(75, 312); // json-array
        op[301] = getOpcodeRnm(76, 317); // json-object
        {int[] a = {303,304,306}; op[302] = getOpcodeCat(a);}
        op[303] = getOpcodeRnm(93, 395); // DQUOTE
        op[304] = getOpcodeRep((char)0, Character.MAX_VALUE, 305);
        op[305] = getOpcodeRnm(85, 355); // iri-char
        op[306] = getOpcodeRnm(93, 395); // DQUOTE
        op[307] = getOpcodeRep((char)1, Character.MAX_VALUE, 308);
        op[308] = getOpcodeRnm(90, 385); // DIGIT
        {int[] a = {310,311}; op[309] = getOpcodeAlt(a);}
        {char[] a = {116,114,117,101}; op[310] = getOpcodeTls(a);}
        {char[] a = {102,97,108,115,101}; op[311] = getOpcodeTls(a);}
        {int[] a = {313,314,316}; op[312] = getOpcodeCat(a);}
        {char[] a = {91}; op[313] = getOpcodeTls(a);}
        op[314] = getOpcodeRep((char)0, Character.MAX_VALUE, 315);
        op[315] = getOpcodeRnm(88, 374); // xdi-char
        {char[] a = {93}; op[316] = getOpcodeTls(a);}
        {int[] a = {318,319,321}; op[317] = getOpcodeCat(a);}
        {char[] a = {123}; op[318] = getOpcodeTls(a);}
        op[319] = getOpcodeRep((char)0, Character.MAX_VALUE, 320);
        op[320] = getOpcodeRnm(88, 374); // xdi-char
        {char[] a = {125}; op[321] = getOpcodeTls(a);}
        {int[] a = {323,324,326}; op[322] = getOpcodeCat(a);}
        {char[] a = {58,105,112,118,54,58}; op[323] = getOpcodeTls(a);}
        op[324] = getOpcodeRep((char)4, (char)4, 325);
        op[325] = getOpcodeRnm(91, 386); // HEXDIG
        op[326] = getOpcodeRep((char)7, Character.MAX_VALUE, 327);
        {int[] a = {328,329}; op[327] = getOpcodeCat(a);}
        {char[] a = {58}; op[328] = getOpcodeTls(a);}
        op[329] = getOpcodeRep((char)4, (char)4, 330);
        op[330] = getOpcodeRnm(91, 386); // HEXDIG
        {int[] a = {332,333,334,335,336,337,338,339,340,341,342}; op[331] = getOpcodeCat(a);}
        {char[] a = {58,117,117,105,100,58}; op[332] = getOpcodeTls(a);}
        op[333] = getOpcodeRnm(79, 343); // time-low
        {char[] a = {45}; op[334] = getOpcodeTls(a);}
        op[335] = getOpcodeRnm(80, 345); // time-mid
        {char[] a = {45}; op[336] = getOpcodeTls(a);}
        op[337] = getOpcodeRnm(81, 347); // time-high
        {char[] a = {45}; op[338] = getOpcodeTls(a);}
        op[339] = getOpcodeRnm(82, 349); // clock-seq
        op[340] = getOpcodeRnm(83, 351); // clock-seq-low
        {char[] a = {45}; op[341] = getOpcodeTls(a);}
        op[342] = getOpcodeRnm(84, 353); // node
        op[343] = getOpcodeRep((char)8, (char)8, 344);
        op[344] = getOpcodeRnm(91, 386); // HEXDIG
        op[345] = getOpcodeRep((char)4, (char)4, 346);
        op[346] = getOpcodeRnm(91, 386); // HEXDIG
        op[347] = getOpcodeRep((char)4, (char)4, 348);
        op[348] = getOpcodeRnm(91, 386); // HEXDIG
        op[349] = getOpcodeRep((char)2, (char)2, 350);
        op[350] = getOpcodeRnm(91, 386); // HEXDIG
        op[351] = getOpcodeRep((char)2, (char)2, 352);
        op[352] = getOpcodeRnm(91, 386); // HEXDIG
        op[353] = getOpcodeRep((char)12, (char)12, 354);
        op[354] = getOpcodeRnm(91, 386); // HEXDIG
        {int[] a = {356,357,358}; op[355] = getOpcodeAlt(a);}
        op[356] = getOpcodeRnm(88, 374); // xdi-char
        op[357] = getOpcodeRnm(87, 367); // context-symbol
        op[358] = getOpcodeRnm(86, 359); // nonparen-delim
        {int[] a = {360,361,362,363,364,365,366}; op[359] = getOpcodeAlt(a);}
        {char[] a = {47}; op[360] = getOpcodeTls(a);}
        {char[] a = {63}; op[361] = getOpcodeTls(a);}
        {char[] a = {35}; op[362] = getOpcodeTls(a);}
        {char[] a = {91}; op[363] = getOpcodeTls(a);}
        {char[] a = {93}; op[364] = getOpcodeTls(a);}
        {char[] a = {39}; op[365] = getOpcodeTls(a);}
        {char[] a = {44}; op[366] = getOpcodeTls(a);}
        {int[] a = {368,369,370,371,372,373}; op[367] = getOpcodeAlt(a);}
        {char[] a = {33}; op[368] = getOpcodeTls(a);}
        {char[] a = {42}; op[369] = getOpcodeTls(a);}
        {char[] a = {61}; op[370] = getOpcodeTls(a);}
        {char[] a = {64}; op[371] = getOpcodeTls(a);}
        {char[] a = {43}; op[372] = getOpcodeTls(a);}
        {char[] a = {36}; op[373] = getOpcodeTls(a);}
        {int[] a = {375,376,377,378,379,380,381}; op[374] = getOpcodeAlt(a);}
        op[375] = getOpcodeRnm(89, 382); // ALPHA
        op[376] = getOpcodeRnm(90, 385); // DIGIT
        {char[] a = {45}; op[377] = getOpcodeTls(a);}
        {char[] a = {46}; op[378] = getOpcodeTls(a);}
        {char[] a = {95}; op[379] = getOpcodeTls(a);}
        {char[] a = {126}; op[380] = getOpcodeTls(a);}
        op[381] = getOpcodeTrg((char)128, (char)65533);
        {int[] a = {383,384}; op[382] = getOpcodeAlt(a);}
        op[383] = getOpcodeTrg((char)65, (char)90);
        op[384] = getOpcodeTrg((char)97, (char)122);
        op[385] = getOpcodeTrg((char)48, (char)57);
        {int[] a = {387,388}; op[386] = getOpcodeAlt(a);}
        op[387] = getOpcodeTrg((char)48, (char)57);
        op[388] = getOpcodeTrg((char)65, (char)70);
        {int[] a = {390,391,392}; op[389] = getOpcodeAlt(a);}
        {char[] a = {13}; op[390] = getOpcodeTbs(a);}
        {char[] a = {10}; op[391] = getOpcodeTbs(a);}
        {int[] a = {393,394}; op[392] = getOpcodeCat(a);}
        {char[] a = {13}; op[393] = getOpcodeTbs(a);}
        {char[] a = {10}; op[394] = getOpcodeTbs(a);}
        {char[] a = {34}; op[395] = getOpcodeTbs(a);}
        return op;
    }

    public static void display(PrintStream out){
        out.println(";");
        out.println("; xdi2.core.xri3.parser.apg.XDI3Grammar");
        out.println(";");
        out.println("xdi-graph               = xdi-statement *( CRLF xdi-statement )");
        out.println("xdi-statement           = contextual-statement / relational-statement / literal-statement");
        out.println("");
        out.println("contextual-statement    = direct-contextual / inverse-contextual");
        out.println("direct-contextual       = absolute / root-relative / context-relative / class-relative");
        out.println("inverse-contextual      = absolute-inverse / root-inverse  / context-inverse / class-inverse");
        out.println("");
        out.println("absolute                =          outer-root \"/()/\" context");
        out.println("root-relative           =       relative-root \"/()/\" context");
        out.println("context-relative        =             context \"/()/\" relative-context");
        out.println("class-relative          =       class-context \"/()/\" instance-context");
        out.println("absolute-inverse        =          context \"/$is()/\" outer-root");
        out.println("root-inverse            =          context \"/$is()/\" relative-root");
        out.println("context-inverse         = relative-context \"/$is()/\" context");
        out.println("class-inverse           = instance-context \"/$is()/\" class-context");
        out.println("");
        out.println("relational-statement    = inverse-relational / direct-relational");
        out.println("direct-relational       = context \"/\"    context \"/\" context");
        out.println("inverse-relational      = context \"/$is\" context \"/\" context");
        out.println("");
        out.println("literal-statement       = literal-context \"&/\" [ *type-singleton ] \"&/\" json-value");
        out.println("");
        out.println("context                 = ( 1*relative-root [ relative-context ] ) / relative-context");
        out.println("relative-context        = 1*subpath");
        out.println("");
        out.println("class-context           = [ 1*relative-root ] class-path");
        out.println("class-path              = ( subpath class-path ) / class");
        out.println("");
        out.println("instance-context        = 1*instance [ relative-context ]");
        out.println("");
        out.println("literal-context         = [ 1*relative-root ] literal-path");
        out.println("literal-path            = ( subpath literal-path ) / attribute-singleton / attribute-path");
        out.println("attribute-path          = attribute-class 1*instance");
        out.println("");
        out.println("root                    = outer-root / relative-root");
        out.println("outer-root              = \"()\"");
        out.println("relative-root           = peer-root / inner-root / statement-root");
        out.println("peer-root               = xref");
        out.println("inner-root              = \"(\" context \"/\" context \")\"");
        out.println("statement-root          = \"(\" xdi-statement \")\"                          ; ISSUE #1 - works but causes full recursion");
        out.println("");
        out.println("xref                    = \"(\" ( context / 1*iri-char / 1*xdi-char ) \")\"  ; ISSUE #2 - needs revision for IRI compliance & valid XDI addresses");
        out.println("");
        out.println("subpath                 = singleton / ( class [ 1*instance ] ) / definition / variable");
        out.println("subsegment              = root / singleton / class / instance / definition / variable");
        out.println("");
        out.println("singleton               = entity-singleton / attribute-singleton");
        out.println("entity-singleton        = authority-singleton / type-singleton");
        out.println("authority-singleton     = person-singleton / group-singleton");
        out.println("type-singleton          = reserved-type / unreserved-type");
        out.println("person-singleton        = \"=\" ( xref / 1*xdi-char )");
        out.println("group-singleton         = \"@\" ( xref / 1*xdi-char )");
        out.println("reserved-type           = \"$\" 1*xdi-char");
        out.println("unreserved-type         = \"+\" ( xref / uuid-literal / ipv6-literal / 1*xdi-char )");
        out.println("attribute-singleton     = \"<\" type-singleton \">\"");
        out.println("");
        out.println("class                   = meta-class / concrete-class");
        out.println("");
        out.println("meta-class              = reserved-meta-class / unreserved-meta-class");
        out.println("reserved-meta-class     = \"[$]\"");
        out.println("unreserved-meta-class   = \"[+]\"");
        out.println("");
        out.println("concrete-class          = entity-class / attribute-class");
        out.println("entity-class            = authority-class / type-class / instance-class");
        out.println("authority-class         = person-class / group-class");
        out.println("type-class              = reserved-class / unreserved-class");
        out.println("instance-class          = mutable-id-class / immutable-id-class");
        out.println("reserved-class          = \"[\" reserved-type \"]\"");
        out.println("unreserved-class        = \"[\" unreserved-type \"]\"");
        out.println("person-class            = \"[=]\"");
        out.println("group-class             = \"[@]\"");
        out.println("mutable-id-class        = \"[*]\"");
        out.println("immutable-id-class      = \"[!]\"");
        out.println("attribute-class         = \"[\" attribute-singleton \"]\"");
        out.println("");
        out.println("instance                = ordered-instance / unordered-instance");
        out.println("ordered-instance        = \"#\" 1*DIGIT");
        out.println("unordered-instance      = mutable-id / immutable-id");
        out.println("mutable-id              = \"*\" ( xref / uuid-literal / ipv6-literal / 1*xdi-char )");
        out.println("immutable-id            = \"!\" ( xref / uuid-literal / ipv6-literal / 1*xdi-char )");
        out.println("");
        out.println("definition              = authority-definition / type-definition");
        out.println("authority-definition    = \"$(\" ( authority-singleton / authority-path ) \")\"");
        out.println("authority-path          = authority-class 1*instance");
        out.println("type-definition         = entity-definition / attribute-definition");
        out.println("entity-definition       = \"$(\" type-singleton \")\"");
        out.println("attribute-definition    = \"$(\" attribute-singleton \")\"");
        out.println("");
        out.println("variable                = \"{\" [ context ] \"}\"        ; ISSUE #3 - this needs a full definition");
        out.println("");
        out.println("json-value              = json-string / json-number / json-boolean / json-array / json-object");
        out.println("json-string             = DQUOTE *iri-char DQUOTE    ; ISSUE #4A - needs real JSON ABNF");
        out.println("json-number             = 1*DIGIT                    ; ISSUE #4B - needs real JSON ABNF");
        out.println("json-boolean            = \"true\" / \"false\"");
        out.println("json-array              = \"[\" *xdi-char \"]\"          ; ISSUE #4C - needs real JSON ABNF");
        out.println("json-object             = \"{\" *xdi-char \"}\"          ; ISSUE #4D - needs real JSON ABNF");
        out.println("");
        out.println("ipv6-literal            = \":ipv6:\" 4HEXDIG 7*( \":\" 4HEXDIG )    ; ISSUE #5 - needs revision into canonical form per RFC 5952");
        out.println("");
        out.println("uuid-literal            = \":uuid:\" time-low \"-\" time-mid \"-\" time-high \"-\" clock-seq clock-seq-low \"-\" node");
        out.println("time-low                = 8HEXDIG");
        out.println("time-mid                = 4HEXDIG");
        out.println("time-high               = 4HEXDIG");
        out.println("clock-seq               = 2HEXDIG");
        out.println("clock-seq-low           = 2HEXDIG");
        out.println("node                    = 12HEXDIG");
        out.println("");
        out.println("iri-char                = xdi-char / context-symbol / nonparen-delim         ; ISSUE #6 - \"(\" and \")\" are excluded");
        out.println("nonparen-delim          = \"/\" / \"?\" / \"#\" / \"[\" / \"]\" / \"'\" / \",\"            ; ISSUE #7 - double quote included?");
        out.println("context-symbol          = \"!\" / \"*\" / \"=\" / \"@\" / \"+\" / \"$\"");
        out.println("xdi-char                = ALPHA / DIGIT / \"-\" / \".\" / \"_\" / \"~\" / %x80-EFFFD ; ISSUE #8 - is there a way to leave high end open?");
        out.println("ALPHA                   = %x41-5A / %x61-7A   ; A-Z, a-z                     ; ISSUE #9A - should we exclude uppercase?");
        out.println("DIGIT                   = %x30-39             ; 0-9");
        out.println("HEXDIG                  = %x30-39 / %x41-46   ; 0-9, A-F                     ; ISSUE #9B - should we exclude uppercase?");
        out.println("CRLF                    = %x0D / %x0A / ( %x0D %x0A )");
        out.println("DQUOTE                  = %x22");
    }
}
