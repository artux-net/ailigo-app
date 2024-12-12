package org.ailingo.app.di

import org.ailingo.app.features.chat.presentation.ChatViewModel
import org.ailingo.app.features.dictionary.main.presentation.DictionaryViewModel
import org.ailingo.app.features.login.presentation.LoginViewModel
import org.ailingo.app.features.profileupdate.presentation.ProfileUpdateViewModel
import org.ailingo.app.features.registration.presentation.RegisterViewModel
import org.ailingo.app.features.registration.presentation.uploadavatar.UploadAvatarViewModel
import org.ailingo.app.features.topics.presentation.TopicViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get(), get(), get(named("tokenRepository"))) }
    viewModel { RegisterViewModel() }
    viewModel { ChatViewModel(get(), get(), get(named("tokenRepository"))) }
    viewModel {
        DictionaryViewModel(
            get(named("dictionaryRepository")),
            get(),
            get(),
            get(named("tokenRepository"))
        )
    }
    viewModel { TopicViewModel(get(), get(), get(named("tokenRepository"))) }
    viewModel { ProfileUpdateViewModel(get(), get(), get(named("tokenRepository"))) }
    viewModel { UploadAvatarViewModel(get(), get(), get(named("tokenRepository"))) }
}