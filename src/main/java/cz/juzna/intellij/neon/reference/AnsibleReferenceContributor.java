package cz.juzna.intellij.neon.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import cz.juzna.intellij.neon.NeonLanguage;
import cz.juzna.intellij.neon.psi.*;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Created by Pavels.Veretennikovs on 2015.05.19..
 */
public class AnsibleReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(jinjaRefPattern(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        String text = element.getText();
                        if (text != null) {
                            return new PsiReference[]{new AnsibleVariableReference(element, new TextRange(0, text.length()))};
                        }
                        return AnsibleVariableReference.EMPTY_ARRAY;
                    }
                });

        registrar.registerReferenceProvider(roleRefPattern(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        String text = element.getText();
                        if (text != null) {
                            return new PsiReference[]{new AnsibleRoleReference(element, new TextRange(0, text.length()))};
                        }
                        return AnsibleVariableReference.EMPTY_ARRAY;
                    }
                });

        registrar.registerReferenceProvider(srcRefPattern(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        String text = element.getText();
                        if (text != null) {
                            return new PsiReference[]{new AnsibleFileReference(element, new TextRange(0, text.length()))};
                        }
                        return AnsibleVariableReference.EMPTY_ARRAY;
                    }
                });
    }

    public static PsiElementPattern.Capture<NeonReference> jinjaRefPattern() {
        return psiElement(NeonReference.class)
                .inside(NeonJinja.class)
                .withLanguage(NeonLanguage.INSTANCE);
    }

    // { role: ROLE }         OR
    // roles:
    //   -- ROLE
    public static PsiElementPattern.Capture<NeonScalar> roleRefPattern() {
        return psiElement(NeonScalar.class)
                .andOr(
                        psiElement().afterSibling(psiElement(NeonKey.class).withText("role")),
                        psiElement().withSuperParent(2,
                                psiElement(NeonArray.class).afterSibling(psiElement(NeonKey.class).withText("roles"))))
                .withLanguage(NeonLanguage.INSTANCE);
    }

    public static PsiElementPattern.Capture<NeonScalar> srcRefPattern() {
        return psiElement(NeonScalar.class)
                .afterSibling(psiElement(NeonKey.class).andOr(psiElement().withText("src"), psiElement().withText("include"), psiElement().withText("include_vars")))
                .withLanguage(NeonLanguage.INSTANCE);
    }
}