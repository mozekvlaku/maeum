package one.maeum.synapse.icons

import androidx.compose.ui.graphics.vector.ImageVector
import one.maeum.synapse.icons.maeumsynapseicons.Maeum
import kotlin.collections.List as ____KtList

public object MaeumSynapseIcons

private var __AllIcons: ____KtList<ImageVector>? = null

public val MaeumSynapseIcons.AllIcons: ____KtList<ImageVector>
  get() {
    if (__AllIcons != null) {
      return __AllIcons!!
    }
    __AllIcons= listOf(Maeum)
    return __AllIcons!!
  }
