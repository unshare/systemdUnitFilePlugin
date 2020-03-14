package net.sjrx.intellij.plugins.systemdunitfiles.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import net.sjrx.intellij.plugins.systemdunitfiles.UnitFileIcon;
import net.sjrx.intellij.plugins.systemdunitfiles.UnitFileLanguage;
import net.sjrx.intellij.plugins.systemdunitfiles.generated.UnitFileElementTypeHolder;
import net.sjrx.intellij.plugins.systemdunitfiles.psi.UnitFilePropertyType;
import net.sjrx.intellij.plugins.systemdunitfiles.psi.UnitFileSectionGroups;
import net.sjrx.intellij.plugins.systemdunitfiles.semanticdata.SemanticDataRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Completion Contributor for keywords in a section.
 */
public class UnitFileKeyCompletionContributor extends CompletionContributor {


  private SemanticDataRepository sdr = SemanticDataRepository.getInstance();

  /**
   * Default constructor.
   */
  public UnitFileKeyCompletionContributor() {
    extend(CompletionType.BASIC, PlatformPatterns.psiElement(UnitFileElementTypeHolder.KEY).withLanguage(UnitFileLanguage.INSTANCE),
      new CompletionProvider<CompletionParameters>() {

        @Override
        protected void addCompletions(@NotNull CompletionParameters parameters,
                                      @NotNull ProcessingContext context,
                                      @NotNull CompletionResultSet resultSet) {
          UnitFileSectionGroups section = PsiTreeUtil.getParentOfType(parameters.getOriginalPosition(), UnitFileSectionGroups.class);
          if (section == null ) section = PsiTreeUtil.getParentOfType(parameters.getPosition(), UnitFileSectionGroups.class);
          if (section == null) return;

          String sectionName = section.getSectionName();
          Set<String> definedKeys = section.getPropertyList().stream().map(UnitFilePropertyType::getKey).collect(Collectors.toSet());

          for (String keyword : sdr.getDocumentedKeywordsInSection(sectionName)) {
            if (definedKeys.contains(keyword)) continue;
            LookupElementBuilder builder = LookupElementBuilder
                    .create(keyword + "=") // TODO: Replace '=' with InsertionHandler
                    .withPresentableText(keyword)
                    .withIcon(UnitFileIcon.FILE).appendTailText("(Option)", true);

            resultSet.addElement(builder);
          }
        }
      }
    );
  }
  
  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
    super.fillCompletionVariants(parameters, result);
  }
}
