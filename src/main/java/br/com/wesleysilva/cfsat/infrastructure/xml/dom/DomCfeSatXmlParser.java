package br.com.wesleysilva.cfsat.infrastructure.xml.dom;

import br.com.wesleysilva.cfsat.domain.model.Cupom;
import br.com.wesleysilva.cfsat.domain.model.ItemCupom;
import br.com.wesleysilva.cfsat.infrastructure.xml.CfeSatXmlParser;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class DomCfeSatXmlParser implements CfeSatXmlParser {

    private static final DateTimeFormatter D = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public Optional<Cupom> parse(Path xmlPath) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // segurança básica contra XXE
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            Document doc = dbf.newDocumentBuilder().parse(xmlPath.toFile());
            doc.getDocumentElement().normalize();

            // Cancelamento: muitos SAT vêm como outro XML (CFeCanc) ou com tag cancCFe
            String rootName = doc.getDocumentElement().getNodeName();
            if (rootName != null && rootName.toLowerCase().contains("cfecanc")) return Optional.empty();
            if (doc.getElementsByTagName("cancCFe").getLength() > 0) return Optional.empty();

            Element infCFe = first(doc, "infCFe");
            if (infCFe == null) return Optional.empty();

            String chave = attr(infCFe, "Id");
            if (isBlank(chave)) return Optional.empty();

            String numero = textOf(doc, "nCFe"); // ide/nCFe
            String dEmi = textOf(doc, "dEmi");   // ide/dEmi
            String hEmi = textOf(doc, "hEmi");   // ide/hEmi
            String vCFe = textOfUnder(doc, "total", "vCFe"); // total/vCFe (no seu XML é filho direto)

            if (isBlank(numero) || isBlank(dEmi) || isBlank(hEmi) || isBlank(vCFe)) return Optional.empty();

            LocalDateTime dataEmissao = LocalDateTime.parse(dEmi + hEmi, D);
            BigDecimal valorTotal = new BigDecimal(vCFe);

            Cupom cupom = Cupom.builder()
                    .chaveAcesso(chave)
                    .numeroCFe(numero)
                    .dataEmissao(dataEmissao)
                    .valorTotal(valorTotal)
                    .build();

            NodeList dets = doc.getElementsByTagName("det");
            for (int i = 0; i < dets.getLength(); i++) {
                Node n = dets.item(i);
                if (!(n instanceof Element det)) continue;

                Element prod = firstChild(det, "prod");
                if (prod == null) continue;

                String cProd = textOfChild(prod, "cProd");
                String xProd = textOfChild(prod, "xProd");
                String qCom = textOfChild(prod, "qCom");
                String cfop = textOfChild(prod, "CFOP");
                String vItem = textOfChild(prod, "vItem"); // no seu XML: prod/vItem

                if (isBlank(cProd) || isBlank(xProd) || isBlank(qCom) || isBlank(cfop) || isBlank(vItem)) {
                    continue; // item inconsistente -> ignora item, mas mantém cupom
                }

                ItemCupom item = ItemCupom.builder()
                        .codigo(cProd.trim())
                        .descricao(xProd.trim())
                        .quantidade(new BigDecimal(qCom))
                        .cfop(cfop.trim())
                        .valorTotalBruto(new BigDecimal(vItem))
                        .build();

                cupom.addItem(item);
            }

            return Optional.of(cupom);

        } catch (Exception e) {
            // XML quebrado? ignora só esse arquivo
            return Optional.empty();
        }
    }

    private static Element first(Document doc, String tag) {
        NodeList nl = doc.getElementsByTagName(tag);
        if (nl.getLength() == 0) return null;
        Node n = nl.item(0);
        return (n instanceof Element) ? (Element) n : null;
    }

    private static String textOf(Document doc, String tag) {
        Element el = first(doc, tag);
        return el == null ? null : el.getTextContent();
    }

    private static String textOfUnder(Document doc, String parentTag, String childTag) {
        NodeList parents = doc.getElementsByTagName(parentTag);
        for (int i = 0; i < parents.getLength(); i++) {
            Node p = parents.item(i);
            if (!(p instanceof Element pe)) continue;
            Element child = firstChild(pe, childTag);
            if (child != null) return child.getTextContent();
        }
        return null;
    }

    private static Element firstChild(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() == 0) return null;
        Node n = nl.item(0);
        return (n instanceof Element) ? (Element) n : null;
    }

    private static String textOfChild(Element parent, String tag) {
        Element el = firstChild(parent, tag);
        return el == null ? null : el.getTextContent();
    }

    private static String attr(Element el, String name) {
        return el.hasAttribute(name) ? el.getAttribute(name) : null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}